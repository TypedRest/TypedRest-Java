package net.typedrest.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import tools.jackson.databind.DeserializationFeature
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.kotlinModule
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody

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

    override fun <T> serialize(entity: T, type: Class<T>): RequestBody =
        mapper.writeValueAsString(entity).toRequestBody(mediaTypeXml)

    override fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody =
        mapper.writerFor(listType(type)).writeValueAsString(entities.toList()).toRequestBody(mediaTypeXml)

    override fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        body.byteStream().use { mapper.readerFor(type).readValue(it) }

    override fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        body.byteStream().use { mapper.readerFor(listType(type)).readValue(it) }

    private fun <T> listType(type: Class<T>) =
        mapper.typeFactory.constructCollectionType(List::class.java, type)
}
