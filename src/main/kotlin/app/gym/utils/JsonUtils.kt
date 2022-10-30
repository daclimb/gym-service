package app.gym.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class JsonUtils {
    companion object {
        private val mapper = jacksonObjectMapper()

        fun toJson(obj: Any?): String {
            return try {
                mapper.writeValueAsString(obj)
            } catch (e: JsonProcessingException) {
                throw RuntimeException("Failed to convert object to JSON string", e)
            }
        }
    }
}