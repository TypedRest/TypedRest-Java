package net.typedrest.endpoints

import net.typedrest.http.ResponseCache

/**
 * Endpoint that caches the last response.
 */
interface CachingEndpoint {
    /**
     * A cached copy of the last response.
     */
    var responseCache: ResponseCache?
}
