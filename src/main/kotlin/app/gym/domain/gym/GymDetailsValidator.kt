package app.gym.domain.gym

import org.springframework.validation.BindException
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.util.regex.Pattern

class GymDetailsInvalidValueException(target: Any, objectName: String) : BindException(target, objectName)

class GymDetailsValidator: Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return GymDetails::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val details = target as GymDetails

        if(details.phoneNumber != null) {
            val pattern = Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}\$")
            val matcher = pattern.matcher(details.phoneNumber)
            if(!matcher.matches()) {
                errors.rejectValue("phoneNumber", "invalid phone number pattern")
            }
        }
    }
}
