package net.typedrest.http

import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.ByteString
import java.text.SimpleDateFormat
import java.util.*

/**
 * Captures the content of a [Response] for caching.
 */
class ResponseCache private constructor(response: Response) {
    companion object {
        /**
         * Creates a [ResponseCache] from a [response] if it is eligible for caching.
         * @return The [ResponseCache]; `null` if the response is not eligible for caching.
         */
        @JvmStatic
        fun from(response: Response): ResponseCache? =
            if (response.isSuccessful && response.code != HttpStatusCode.NoContent.code && !response.cacheControl.noStore) {
                ResponseCache(response)
            } else null

        private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("GMT") }
    }

    private val bodyByteString = response.body.byteString()
    private val contentType = response.body.contentType()

    /**
     * Returns a copy of the cached [RequestBody].
     */
    fun getBody() = bodyByteString.toResponseBody(contentType)

    private var expires =
        if (response.cacheControl.noCache) {
            Date() // Treat no-cache as expired immediately
        } else {
            response.headers.getDate("Expires")
                ?: response.cacheControl.maxAgeSeconds.let { maxAge ->
                    if (maxAge == -1) null else Date(System.currentTimeMillis() + maxAge * 1000)
                }
                ?: if (response.cacheControl.noCache) Date() else null
        }

    /**
     * Indicates whether this cached response has expired.
     */
    val isExpired: Boolean
        get() = expires?.before(Date()) ?: false

    private val eTag: String? = response.header("ETag")
    private val lastModified: Date? = response.headers.getDate("Last-Modified")

    /**
     * Returns request headers that require that the resource has been modified since it was cached.
     */
    fun ifModifiedHeaders(): Headers {
        val builder = Headers.Builder()
        eTag?.let { builder.add("If-None-Match", it) }
            ?: lastModified?.let { builder.add("If-Modified-Since", dateFormat.format(it)) }
        return builder.build()
    }

    /**
     * Returns request headers that require that the resource has not been modified since it was cached.
     */
    fun ifUnmodifiedHeaders(): Headers {
        val builder = Headers.Builder()
        eTag?.let { builder.add("If-Match", it) }
            ?: lastModified?.let { builder.add("If-Unmodified-Since", dateFormat.format(it)) }
        return builder.build()
    }
}
