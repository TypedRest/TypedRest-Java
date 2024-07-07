package net.typedrest.endpoints.generic

import net.typedrest.endpoints.*
import net.typedrest.errors.*
import net.typedrest.http.*
import okhttp3.*
import java.net.URI

/**
 * Base class for building endpoints that use ETags and Last-Modified timestamps for caching and to avoid lost updates.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the referrer's.
 */
abstract class AbstractCachingEndpoint(referrer: Endpoint, relativeUri: URI) :
    AbstractEndpoint(referrer, relativeUri), CachingEndpoint {
    override var responseCache: ResponseCache? = null

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
        val cache = responseCache // Copy reference for thread-safety
        val headers = responseCache?.ifModifiedHeaders() ?: Headers.Builder().build()
        val response = httpClient.newCall(Request.Builder().get().uri(uri).headers(headers).build()).execute()

        return if (response.code == HttpStatusCode.NotModified.code && cache != null && !cache.isExpired)
            cache.getBody()
        else {
            responseCache = ResponseCache.from(handle(response))
            responseCache?.getBody()
        }
    }

    /**
     * Performs an [HttpMethod.PUT] request on the [Endpoint.uri].
     *
     * Sends If-Match header if there is a cached ETag to detect lost updates.
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
        responseCache = null
        return execute(Request.Builder().put(content).uri(uri).headers(headers).build())
    }

    /**
     * Performs an [HttpMethod.DELETE] request on the [Endpoint.uri].
     *
     * Sends If-Match header if there is a cached ETag to detect lost updates.
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
        responseCache = null
        execute(Request.Builder().delete().uri(uri).headers(headers).build()).close()
    }
}
