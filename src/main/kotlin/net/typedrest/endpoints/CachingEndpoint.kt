package net.typedrest.endpoints

/**
 * Endpoint that caches the last response.
 */
interface CachingEndpoint {
    /**
     * A cached copy of the last response.
     */
    responseCache?: ResponseCache
}
