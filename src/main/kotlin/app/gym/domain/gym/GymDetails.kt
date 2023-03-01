package app.gym.domain.gym

import app.gym.util.JsonUtils
import com.fasterxml.jackson.annotation.JsonProperty

data class GymDetails(
    val phoneNumber: String? = null,
    val instagram: String? = null,
    val prices: List<Price>? = null,
    val grades: List<String>? = null,
    val services: List<String>? = null,
    val trainings: List<String>? = null,
    val openingHours: OpeningHours? = null,
    val floorArea: Int? = null,
) {
    companion object {
        fun from(detailsString: String): GymDetails {
            return if (detailsString == "") {
                GymDetails()
            } else {
                JsonUtils.fromJson(detailsString, GymDetails::class.java)
            }
        }
    }

    class Price(
        val type: String,
        val price: Int,
    )

    class OpeningHours(
        @JsonProperty("월") val mon: List<String>,
        @JsonProperty("화") val tue: List<String>,
        @JsonProperty("수") val wed: List<String>,
        @JsonProperty("목") val thu: List<String>,
        @JsonProperty("금") val fri: List<String>,
        @JsonProperty("토") val sat: List<String>,
        @JsonProperty("일") val sun: List<String>,
        @JsonProperty("공휴일") val hol: List<String>,
    )
}
