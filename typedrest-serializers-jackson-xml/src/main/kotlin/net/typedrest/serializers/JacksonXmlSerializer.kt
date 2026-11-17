package net.typedrest.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.DeserializationFeature
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.kotlinModule
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as XML using Jackson.
 *
 * @param mapper The Jackson XML mapper to use for serializing and deserializing. Defaults to omitting `null` properties when writing and tolerating unknown properties when reading.
 */
open class JacksonXmlSerializer @JvmOverloads constructor(
    private val mapper: XmlMapper = defaultMapper()
) : AbstractXmlSerializer() {
    companion object {
        @JvmStatic
        fun defaultMapper(): XmlMapper =
            XmlMapper.builder()
                .addModule(kotlinModule())
                .changeDefaultPropertyInclusion { it.withValueInclusion(JsonInclude.Include.NON_NULL) }
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build()
    }

    override fun <T> serialize(entity: T, type: Type): RequestBody =
        // Plain classes are written by their runtime type; generic types need the declared type, e.g., for the root element of lists
        (if (type is Class<*>) mapper.writeValueAsString(entity) else mapper.writerFor(mapper.typeFactory.constructType(type)).writeValueAsString(entity))
            .toRequestBody(mediaTypeXml)

    override fun <T> deserialize(body: ResponseBody, type: Type): T? =
        body.byteStream().use { mapper.readerFor(mapper.typeFactory.constructType(type)).readValue(it) }
}
