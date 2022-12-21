package app.gym.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyFactory
import java.security.KeyPair
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(val authenticationConfiguration: AuthenticationConfiguration) {

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun jwtAuthenticationProvider(keyPair: KeyPair) = JwtAuthenticationProvider(keyPair)

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer {
            it.ignoring()
                .antMatchers(*PUBLIC_ENDPOINTS)
        }
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()

            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/api/gym", "/api/gym/**").permitAll()
//            .anyRequest().authenticated()

            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }

    @Bean
    fun jwtKeyPair(
        @Value("\${jwt.key.public}")
        publicKeyString: String,
        @Value("\${jwt.key.private}")
        privateKeyString: String
    ): KeyPair {
        val keyFactory = KeyFactory.getInstance("RSA")

        val publicBytes = Base64.getDecoder().decode(publicKeyString)
        val publicKeySpec = X509EncodedKeySpec(publicBytes)
        val publicKey = keyFactory.generatePublic(publicKeySpec)

        val privateBytes = Base64.getDecoder().decode(privateKeyString)
        val privateKeySpec = PKCS8EncodedKeySpec(privateBytes)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        return KeyPair(publicKey, privateKey)
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}