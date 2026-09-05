package net.typedrest.serializers

import okhttp3.MediaType.Companion.toMediaType

/**
 * Common base class for XML serializers.
 */
abstract class AbstractXmlSerializer : Serializer {
    companion object {
        @JvmStatic
        protected val mediaTypeXml = "application/xml".toMediaType()

        @JvmStatic
        protected val mediaTypeTextXml = "text/xml".toMediaType()
    }

    override val supportedMediaTypes = listOf(mediaTypeXml, mediaTypeTextXml)
}
