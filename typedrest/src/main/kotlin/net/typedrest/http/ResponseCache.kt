package net.typedrest.http

import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.ByteString
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Captures the content of a [Response] for caching.
 */
class ResponseCache private constructor(
    response: Response,
    private val bodyByteString: ByteString,
    private val contentType: MediaType?,
    private val lastModified: Date? = response.headers.getDate("Last-Modified")
) {
    companion object {
        /**
         * Creates a [ResponseCache] from [response] if it is eligible for caching.
         *
         * The response body is consumed (buffered) by this call. If you also need the body for other purposes, buffer it first and use the [from] overload that accepts a pre-buffered [ByteString].
         *
         * @param response The HTTP response whose body will be consumed and cached.
         * @return The [ResponseCache]; `null` if the response is not eligible for caching.
         */
        @JvmStatic
        fun from(response: Response): ResponseCache? =
            if (isEligible(response)) {
                ResponseCache(response, response.body.byteString(), response.body.contentType())
            } else null

        /**
         * Creates a [ResponseCache] from [response] using a pre-buffered body if the response is eligible for caching.
         *
         * Use this overload when the body has already been read and must remain available for other consumers after this call returns.
         *
         * @param response The HTTP response whose headers and status code determine eligibility.
         * @param bodyByteString The already-buffered response body bytes.
         * @param contentType The media type of the body, used when reconstructing the [ResponseBody].
         * @return The [ResponseCache]; `null` if the response is not eligible for caching.
         */
        @JvmStatic
        fun from(response: Response, bodyByteString: ByteString, contentType: MediaType?): ResponseCache? =
            if (isEligible(response)) {
                ResponseCache(response, bodyByteString, contentType)
            } else null

        /**
         * Creates a [ResponseCache] from the [response] to a PUT request if the server sent an ETag. Caches the response body if there is one.
         *
         * @param response The response to the PUT request, whose headers and status code determine eligibility.
         * @param responseBody The already-buffered response body bytes.
         * @param responseContentType The media type of [responseBody].
         * @param requestBody The body of the PUT request.
         * @param requestContentType The media type of [requestBody].
         * @return The [ResponseCache]; `null` if the response is not eligible for caching.
         */
        @JvmStatic
        fun fromPut(
            response: Response,
            responseBody: ByteString,
            responseContentType: MediaType?,
            requestBody: ByteString,
            requestContentType: MediaType?
        ): ResponseCache? = when {
            !response.isSuccessful || response.cacheControl.noStore || response.header("ETag") == null -> null
            responseBody.size == 0 -> ResponseCache(response, requestBody, requestContentType, lastModified = null)
            else -> ResponseCache(response, responseBody, responseContentType)
        }

        private fun isEligible(response: Response): Boolean =
            response.isSuccessful && response.code != HttpStatusCode.NoContent.code && !response.cacheControl.noStore

        private val dateFormat: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)

        private fun Date.formatHttp(): String =
            ZonedDateTime.ofInstant(toInstant(), ZoneOffset.UTC).format(dateFormat)
    }

    /**
     * Returns a new [ResponseBody] backed by the cached bytes.
     *
     * Each call produces an independent copy so the body can be consumed multiple times.
     */
    fun getBody() = bodyByteString.toResponseBody(contentType)

    private val eTag: String? = response.header("ETag")

    /**
     * Builds conditional request headers asserting that the resource *has* been modified since it was cached
     *
     * Suitable for GET/HEAD requests that should bypass the cache.
     */
    fun ifModifiedHeaders(): Headers {
        val builder = Headers.Builder()
        eTag?.let { builder.add("If-None-Match", it) }
            ?: lastModified?.let { builder.add("If-Modified-Since", it.formatHttp()) }
        return builder.build()
    }

    /**
     * Builds conditional request headers asserting that the resource has *not* been modified since it was cached.
     *
     * Suitable for PUT/DELETE requests to prevent lost updates.
     */
    fun ifUnmodifiedHeaders(): Headers {
        val builder = Headers.Builder()
        if (eTag != null) {
            if (!eTag.startsWith("W/")) builder.add("If-Match", eTag)
        } else {
            lastModified?.let { builder.add("If-Unmodified-Since", it.formatHttp()) }
        }
        return builder.build()
    }
}
