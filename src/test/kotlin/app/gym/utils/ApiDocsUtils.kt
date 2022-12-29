package app.gym.utils

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.web.servlet.ResultActions

fun ResultActions.andDocument(
    identifier: String,
    builder: ResourceSnippetParametersBuilder.() -> Unit
) {
    this.andDo(
        MockMvcRestDocumentationWrapper
            .document(
                identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                ResourceDocumentation.resource(
                    ResourceSnippetParametersBuilder().apply(builder).build()
                )
            )
    )
}