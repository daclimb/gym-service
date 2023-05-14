package restdocs

import com.epages.restdocs.apispec.*
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.ResultActions

fun ResultActions.andDocument(
    identifier: String,
    builder: RestdocsBuilder.() -> Unit
) {
    this.andDo(
        MockMvcRestDocumentationWrapper.document(
            identifier,
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            ResourceDocumentation.resource(
                RestdocsBuilder().apply(builder).build()
            )
        )
    )
}

sealed class RestdocsType {
    object STRING: RestdocsType()
    object NUMBER: RestdocsType()
    object BOOLEAN: RestdocsType()
    object OBJECT: RestdocsType()

    open class Array(
        internal val itemsType: String
    ): RestdocsType()

    object STRING_ARRAY: Array("string")
    object NUMBER_ARRAY: Array("number")
    object BOOLEAN_ARRAY: Array("boolean")
    object OBJECT_ARRAY: Array("object")

    class ENUM<T: Enum<T>>(internal val type: Enum<T>): RestdocsType()

    fun toSimpleType(): SimpleType {
        return when(this) {
            STRING -> SimpleType.STRING
            NUMBER -> SimpleType.INTEGER
            BOOLEAN -> SimpleType.BOOLEAN
            else -> throw Error("Cannot convert to simple type")
        }
    }

    fun toJsonType(): JsonFieldType {
        return when(this) {
            STRING -> JsonFieldType.STRING
            NUMBER -> JsonFieldType.NUMBER
            BOOLEAN -> JsonFieldType.BOOLEAN
            is Array -> JsonFieldType.ARRAY
            is ENUM<*> -> JsonFieldType.ARRAY
            else -> throw Error("Cannot convert to json field type")
        }
    }
}

class RestdocsBuilder {

    var tags = emptySet<String>()
    private var request: Request? = null
    private var response: Response? = null

    fun build(): ResourceSnippetParameters {
        val _tags = this.tags
        return ResourceSnippetParametersBuilder().apply {
            tags(*_tags.toTypedArray())
            buildRequest(this)
            buildResponse(this)
        }.build()
    }

    private fun buildRequest(builder: ResourceSnippetParametersBuilder) {
        this.request?.run {
            if (this.name != null) {
                builder.requestSchema(Schema(this.name))
            }
            val params = this.pathParams.map {
                parameterWithName(it.name).type(it.type.toSimpleType()).description(it.description)
            }
            builder.pathParameters(params)
            builder.requestFields(toRestdocsFields(this.fields))
        }
    }

    private fun buildResponse(builder: ResourceSnippetParametersBuilder) {
        this.response?.run {
            if (name != null) {
                builder.responseSchema(Schema(this.name))
            }
            builder.responseFields(toRestdocsFields(this.fields))
        }
    }

    private fun toRestdocsFields(fields: List<Field>): List<FieldDescriptor> {
        return fields.map {
            val type = it.type
            val field = when (it.optional) {
                true -> fieldWithPath(it.name)
                    .description(it.description).optional()
                false -> fieldWithPath(it.name)
                    .description(it.description)
            }


            if (type is RestdocsType.Array) {
                field.attributes["itemsType"] = type.itemsType
            } else if (type is RestdocsType.ENUM<*>) {
                field.type("enum")
                field.attributes["enumValues"] = type.type::class.java.enumConstants.joinToString(",")
            } else {
                field.type(type.toJsonType())
            }
            field
        }
    }

    fun request(name: String? = null, block: Request.() -> Unit) {
        this.request = Request(name).apply(block)
    }

    fun response(name: String? = null, block: Response.() -> Unit) {
        this.response = Response(name).apply(block)
    }

    class Request(
        internal val name: String? = null
    ) {
        internal val pathParams = mutableListOf<PathParam>()
        internal val fields = mutableListOf<Field>()

        private var prefix = ""

        fun pathParam(name: String, builder: PathParam.() -> Unit) {
            this.pathParams.add(PathParam(name).apply(builder))
        }

        fun field(name: String, builder: Field.() -> Unit) {
            val fieldName = if (prefix.isNotEmpty()) {
                "$prefix.$name"
            } else {
                name
            }
            this.fields.add(Field(fieldName).apply(builder))
        }

        fun array(name: String, builder: Request.() -> Unit) {
            this.prefixed("$name[]", builder)
        }

        fun prefixed(prefix: String, builder: Request.() -> Unit) {
            val old = this.prefix
            this.prefix = "$old.$prefix"
            this.apply(builder)
            this.prefix = old
        }
    }

    class PathParam(
        internal val name: String
    ) {
        var type: RestdocsType = RestdocsType.STRING
        var description: String? = null
    }

    class Response(
        internal val name: String? = null
    ) {
        internal val fields = mutableListOf<Field>()

        private var prefix = ""

        fun field(name: String, builder: Field.() -> Unit) {
            val fieldName = if (prefix.isNotEmpty()) {
                "$prefix.$name"
            } else {
                name
            }
            this.fields.add(Field(fieldName).apply(builder))
        }

        fun array(name: String, builder: Response.() -> Unit) {
            this.prefixed("$name[]", builder)
        }

        fun prefixed(prefix: String, builder: Response.() -> Unit) {
            val old = this.prefix
            this.prefix = "$old.$prefix"
            this.apply(builder)
            this.prefix = old
        }
    }

    class Field(
        internal val name: String
    ) {
        var type: RestdocsType = RestdocsType.STRING
        var description: String? = null
        var optional: Boolean = false
    }
}