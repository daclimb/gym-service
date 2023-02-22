package app.gym.api.controller

import app.gym.api.response.ClientErrorResponse
import app.gym.domain.franchise.FranchiseNotFoundException
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.ImageNotFoundException
import app.gym.domain.member.DuplicatedEmailException
import app.gym.domain.member.EmailOrPasswordNotMatchedException
import app.gym.domain.member.MemberNotFoundException
import app.gym.domain.member.PasswordNotMatchedException
import app.gym.domain.tag.TagDuplicatedException
import app.gym.domain.tag.TagNotExistsException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handleEmailOrPasswordNotMatchedException(exception: EmailOrPasswordNotMatchedException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: email or password not matched")
        )
    }

    @ExceptionHandler
    fun handleGymNotFoundException(exception: GymNotFoundException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: gym not found")
        )
    }

    @ExceptionHandler
    fun handlerFranchiseNotFoundException(exception: FranchiseNotFoundException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: franchise not found")
        )
    }

    @ExceptionHandler
    fun handlerDuplicatedEmailException(exception: DuplicatedEmailException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("email already exists")
        )
    }

    @ExceptionHandler
    fun handlerPasswordNotMatchedException(exception: PasswordNotMatchedException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: password not matched")
        )
    }

    @ExceptionHandler
    fun handlerMemberNotFoundException(exception: MemberNotFoundException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: member not found")
        )
    }

    @ExceptionHandler
    fun handlerTagDuplicatedException(exception: TagDuplicatedException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: tag already exists")
        )
    }

    @ExceptionHandler
    fun handlerTagNotExistsException(exception: TagNotExistsException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: tag not exists")
        )
    }

    @ExceptionHandler
    fun handlerImageNotFoundException(exception: ImageNotFoundException): ResponseEntity<ClientErrorResponse> {
        return ResponseEntity.badRequest().body(
            ClientErrorResponse("Failure: image not found")
        )
    }
}