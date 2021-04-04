package net.typedrest.http

import okhttp3.Headers
import okhttp3.RequestBody
import okhttp3.Response
import java.net.URI

/**
 * Communicates with remote resources using JavaScript's standard Fetch API.
 */
class FetchHttpClient : HttpClient {
    override val defaultHeaders: Headers
        get() = TODO("Not yet implemented")

    override fun send(uri: URI, method: HttpMethod, headers: Headers?, body: RequestBody?): Response {
        val mergedHeaders = Headers(headers)
        this.defaultHeaders.forEach((value, key) => {
            if (!mergedHeaders.has(key)) {
                mergedHeaders.set(key, value)
            }
        })

        return fetch(uri.href, { method, headers: mergedHeaders, body })
    }
}
