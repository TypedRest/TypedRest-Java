package net.typedrest.http

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.InputStream

/**
 * A [RequestBody] that streams its content from an [InputStream] without buffering the entire payload in memory.
 *
 * @param contentType The MIME type of the body, or `null` if unknown.
 * @param stream The input stream to read the body from. Will be consumed when the request is sent.
 */
internal class InputStreamRequestBody(
    private val contentType: MediaType?,
    private val stream: InputStream
) : RequestBody() {
    override fun contentType(): MediaType? = contentType

    // The stream can only be read once, so disable retries.
    override fun isOneShot(): Boolean = true

    override fun writeTo(sink: BufferedSink) {
        stream.source().use { sink.writeAll(it) }
    }
}
