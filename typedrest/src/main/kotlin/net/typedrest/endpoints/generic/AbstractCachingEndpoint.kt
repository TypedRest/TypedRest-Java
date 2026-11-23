package net.typedrest.endpoints.generic

import net.typedrest.endpoints.*
import net.typedrest.errors.*
import net.typedrest.http.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.net.URI

/**
 * Base class for building endpoints that use ETags and Last-Modified timestamps for caching and to avoid lost updates.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 */
abstract class AbstractCachingEndpoint(referrer: Endpoint, relativeUri: URI) :
    AbstractEndpoint(referrer, relativeUri), CachingEndpoint {
    @Volatile
    override var responseCache: ResponseCache? = null

    // Track writes, so responses to requests that overlap a write do not cache outdated content.
    private var writesInProgress = 0
    private var finishedWrites = 0
    private val cacheLock = Any()

    private fun startWrite(): Int = synchronized(cacheLock) {
        responseCache = null
        writesInProgress++
        finishedWrites
    }

    private fun endWrite(finishedWrites: Int, cache: ResponseCache? = null) {
        synchronized(cacheLock) {
            writesInProgress--
            if (cache != null && writesInProgress == 0 && this.finishedWrites == finishedWrites)
                responseCache = cache
            this.finishedWrites++
        }
    }

    private fun storeResponseCache(cache: ResponseCache?, finishedWrites: Int) = synchronized(cacheLock) {
        if (writesInProgress == 0 && this.finishedWrites == finishedWrites)
            responseCache = cache
    }

    /**
     * Sends a request that modifies the resource and discards [responseCache].
     *
     * @param request Sends the request.
     * @return The result of [request].
     */
    protected fun <T> trackWrite(request: () -> T): T {
        val finishedWrites = startWrite()
        try {
            return request()
        } finally {
            endWrite(finishedWrites)
        }
    }

    /**
     * Performs an HTTP GET request on the [Endpoint.uri] and caches the response if the server sends an ETag.
     *
     * Sends If-None-Match header if there is already a cached ETag.
     *
     * @return The response of the request or the cached response if the server responded with [HttpStatusCode.NotModified].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    protected fun getContent(): ResponseBody? {
        // Keep the cache entry the request was based on, since a concurrent write may replace or discard it
        val (cache, finishedWrites) = synchronized(cacheLock) { responseCache to finishedWrites }

        val headers = cache?.ifModifiedHeaders() ?: Headers.Builder().build()
        return httpClient.newCall(Request.Builder().get().uri(uri).headers(headers).build()).execute().use { response ->
            if (response.code == HttpStatusCode.NotModified.code && cache != null) {
                cache.getBody()
            } else {
                handle(response)
                val bodyByteString = response.body.byteString()
                val contentType = response.body.contentType()
                storeResponseCache(ResponseCache.from(response, bodyByteString, contentType), finishedWrites)
                bodyByteString.toResponseBody(contentType)
            }
        }
    }

    /**
     * Performs an [HttpMethod.PUT] request on the [Endpoint.uri].
     *
     * Sends If-Match header if there is a cached strong ETag to detect lost updates.
     * Caches the response if the server sends an ETag, so later requests can be conditional without reading the resource again.
     *
     * @param content The content to send to the server.
     * @return The response message.
     * @throws ConflictException The content has changed since it was last retrieved with [getContent]. Your changes were rejected to prevent a lost update.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    protected fun putContent(content: RequestBody): Response {
        val headers = responseCache?.ifUnmodifiedHeaders() ?: Headers.Builder().build()
        val finishedWrites = startWrite()
        var cache: ResponseCache? = null
        try {
            // Buffer the request body before sending, so it is still available for caching afterwards
            val requestBody = Buffer().also { content.writeTo(it) }.readByteString()
            val requestContentType = content.contentType()

            val response = execute(Request.Builder().put(requestBody.toRequestBody(requestContentType)).uri(uri).headers(headers).build())
            if (response.header("ETag") == null) return response

            val responseContentType = response.body.contentType()
            val responseBody = response.use { it.body.byteString() }
            cache = ResponseCache.fromPut(response, responseBody, responseContentType, requestBody, requestContentType)
            return response.newBuilder().body(responseBody.toResponseBody(responseContentType)).build()
        } finally {
            endWrite(finishedWrites, cache)
        }
    }

    /**
     * Performs an [HttpMethod.DELETE] request on the [Endpoint.uri].
     *
     * Sends If-Match header if there is a cached strong ETag to detect lost updates.
     *
     * @throws ConflictException The content has changed since it was last retrieved with [getContent]. Your changes were rejected to prevent a lost update.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    protected fun deleteContent() {
        val headers = responseCache?.ifUnmodifiedHeaders() ?: Headers.Builder().build()
        trackWrite { execute(Request.Builder().delete().uri(uri).headers(headers).build()) }.close()
    }
}
