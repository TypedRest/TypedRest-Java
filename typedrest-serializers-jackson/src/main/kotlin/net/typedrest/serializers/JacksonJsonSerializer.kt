package net.typedrest.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as JSON using Jackson.
 *
 * @param mapper The Jackson object mapper to use for serializing and deserializing. Defaults to omitting `null` properties when writing and tolerating unknown properties when reading.
 */
open class JacksonJsonSerializer @JvmOverloads constructor(
    private val mapper: JsonMapper = defaultMapper()
) : AbstractJsonSerializer() {
    companion object {
        @JvmStatic
        fun defaultMapper(): JsonMapper =
            JsonMapper.builder()
                .addModule(kotlinModule())
                .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build()
    }

    override fun <T> serialize(entity: T, type: Type): RequestBody =
        mapper.writeValueAsString(entity).toRequestBody(mediaTypeJson)

    override fun <T> deserialize(body: ResponseBody, type: Type): T? =
        body.byteStream().use { mapper.readerFor(mapper.typeFactory.constructType(type)).readValue(it) }
}
