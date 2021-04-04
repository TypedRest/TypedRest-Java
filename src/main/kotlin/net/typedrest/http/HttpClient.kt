package net.typedrest.http

import java.net.URI
import okhttp3.*

/**
 * HTTP client used to communicate with remote resources.
 */
interface HttpClient {
    /**
     * Default HTTP headers to set for requests when not explicitly overridden.
     */
    val defaultHeaders: Headers

    /**
     * Sends an HTTP request.
     * @param uri The URI to send the message to.
     * @param headers The HTTP headers to set.
     * @param body The body to send.
     */
    fun send(uri: URI, method: HttpMethod, headers: Headers?, body: RequestBody?): Response
}
