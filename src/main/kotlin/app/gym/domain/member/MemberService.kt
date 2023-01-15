package app.gym.domain.member

import app.gym.security.JwtTokenHandler
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


class DuplicatedEmailException : Exception()
class MemberNotFoundException : Exception()
class PasswordNotMatchedException : Exception()

val logger = KotlinLogging.logger {  }

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenHandler: JwtTokenHandler
) {
    fun signup(command: SignupCommand) {
        // TODO: signup 후 이메일 전송
        if (memberRepository.existsByEmail(command.email)) {
            throw DuplicatedEmailException()
        }
        val password = passwordEncoder.encode(command.password)
        val member = Member(command.email, password, command.name)
        val v = memberRepository.save(member)
        logger.info { "Member created" }
        logger.debug { "with member id = ${v.id}" }
    }

    fun login(command: LoginCommand): String {
        val member = memberRepository.findByEmail(command.email) ?: throw MemberNotFoundException()
        if (!passwordEncoder.matches(command.password, member.password)) {
            throw PasswordNotMatchedException()
        }
        val token = jwtTokenHandler.generateJwtToken(member)
        logger.info { "JWT token published" }
        logger.debug { "with member id = ${member.id}, token = \"$token\"" }
        return token
    }

    fun getMember(memberId: Long): Member {
        return memberRepository.findById(memberId).orElseThrow(::MemberNotFoundException)
    }
}