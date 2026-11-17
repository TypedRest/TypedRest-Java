package net.typedrest.serializers

import kotlinx.serialization.*
import nl.adaptivity.xmlutil.serialization.XML
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as XML using XmlUtil.
 *
 * @param xml The XmlUtil [XML] instance to use. Defaults to XmlUtil's recommended configuration.
 */
open class XmlUtilSerializer @JvmOverloads constructor(
    private val xml: XML = XML.recommended_1_0()
) : AbstractXmlSerializer() {
    override fun <T> serialize(entity: T, type: Type): RequestBody =
        xml.encodeToString(getSerializer(type), entity).toRequestBody(mediaTypeXml)

    override fun <T> deserialize(body: ResponseBody, type: Type): T? =
        xml.decodeFromString(getSerializer<T>(type), body.string())

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSerializer(type: Type) =
        xml.serializersModule.serializer(type) as KSerializer<T>
}
