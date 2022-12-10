package app.gym.config

import app.gym.domain.jwt.InvalidJwtTokenException
import app.gym.domain.jwt.JwtProvider
import app.gym.domain.member.MemberPrincipal
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class InvalidHeaderException : RuntimeException()

val logger = KotlinLogging.logger { }

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
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

        val memberId = try {
            jwtProvider.parseMemberId(token.value)
        } catch (e: InvalidJwtTokenException) {
            logger.warn("$e: Cause of exception is ambiguous.")
            return
        }
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(MemberPrincipal(memberId), null)

        logger.info("Successfully authenticated")
        logger.debug("with member id = $memberId")
        filterChain.doFilter(request, response)
    }
}