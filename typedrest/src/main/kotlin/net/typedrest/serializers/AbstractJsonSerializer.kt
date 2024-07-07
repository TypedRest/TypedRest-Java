package net.typedrest.serializers

import okhttp3.MediaType.Companion.toMediaType

/**
 * Common base class for JSON serializers.
 */
abstract class AbstractJsonSerializer : Serializer {
    companion object {
        @JvmStatic
        protected val mediaTypeJson = "application/json".toMediaType()
    }

    override val supportedMediaTypes = listOf(mediaTypeJson)
}
