package net.typedrest.endpoints.raw

import net.typedrest.endpoints.*
import net.typedrest.http.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.net.URI

/**
 * Endpoint for a binary blob that can be downloaded or uploaded.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the referrer's. Add a "./" prefix here to imply a trailing slash on referrer's URI.
 */
open class BlobEndpointImpl(referrer: Endpoint, relativeUri: URI) : AbstractEndpoint(referrer, relativeUri), BlobEndpoint {
    /**
     * Creates a new blob endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the referrer's. Add a "./" prefix here to imply a trailing slash on referrer's URI.
     */
    constructor(referrer: Endpoint, relativeUri: String) :
        this(referrer, URI(relativeUri))

    override fun probe() =
        execute(Request.Builder().options().uri(uri).build()).close()

    override val isDownloadAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.GET)

    override fun download(): InputStream {
        val response = execute(Request.Builder().get().uri(uri).build())
        return response.body?.byteStream() ?: throw IllegalStateException("Response body is null")
    }

    override val isUploadAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.PUT)

    override fun uploadFrom(stream: InputStream, mimeType: String?) {
        val body = stream.readBytes().toRequestBody(mimeType?.toMediaTypeOrNull())
        execute(Request.Builder().put(body).uri(uri).build()).close()
    }

    override val isDeleteAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.DELETE)

    override fun delete() =
        execute(Request.Builder().delete().uri(uri).build()).close()
}
