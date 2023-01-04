package app.gym.jwt

import app.gym.config.PUBLIC_ENDPOINTS
import app.gym.config.PUBLIC_ENDPOINTS_GET
import mu.KotlinLogging
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InvalidHeaderException : RuntimeException()

val logger = KotlinLogging.logger { }

@Component
class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cookies = request.cookies
        if (cookies.isNullOrEmpty()) {
            logger.info { "Cookie is null or empty." }
            response.status = HttpStatus.UNAUTHORIZED.value()
            return
        }

        val token = cookies.find { it.name == "jwt" }
        if (token == null) {
            logger.info { "Cookie has no jwt token." }
            response.status = HttpStatus.UNAUTHORIZED.value()
            return
        }
        val jwtAuthenticationToken = JwtAuthenticationToken(token.value)
        val authentication = authenticationManager.authenticate(jwtAuthenticationToken)
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val antPathMatcher = AntPathMatcher()
        return PUBLIC_ENDPOINTS.any { antPathMatcher.match(it, request.requestURI) } ||
                (request.method == HttpMethod.GET.toString() && PUBLIC_ENDPOINTS_GET.any {
                    antPathMatcher.match(
                        it,
                        request.requestURI
                    )
                })
    }
}