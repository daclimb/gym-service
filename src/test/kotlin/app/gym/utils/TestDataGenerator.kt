package app.gym.utils

import app.gym.api.request.AddGymRequest
import app.gym.domain.gym.Gym
import app.gym.domain.image.Image
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.test.web.servlet.ResultActions
import java.util.*

fun ResultActions.andDocument(
    identifier: String,
    builder: ResourceSnippetParametersBuilder.() -> Unit
) {
    this.andDo(
        MockMvcRestDocumentationWrapper
            .document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                resource(
                    ResourceSnippetParametersBuilder().apply(builder).build()
                )
            )
    )
}

class TestDataGenerator {
    companion object {
        fun gym(
            id: Long? = null,
            name: String = "name",
            address: String = "address",
            description: String = "description",
            images: MutableList<Image> = mutableListOf(Image.create(UUID.randomUUID(), "image"))
        ): Gym {
            val gym = Gym(id)
            gym.name = name
            gym.address = address
            gym.description = description
            gym.images = images

            return gym
        }

        fun gyms(
            len: Long = 3L
        ): List<Gym> {
            val gyms = mutableListOf<Gym>()
            for (i: Long in 1L..len)
                gyms.add(gym(i))
            return gyms.toList()
        }

        fun addGymRequest(
            name: String = "name",
            address: String = "address",
            description: String = "description",
            imageIds: List<UUID> = emptyList()
        ): AddGymRequest {
            return AddGymRequest(name, address, description, imageIds)
        }
    }
}