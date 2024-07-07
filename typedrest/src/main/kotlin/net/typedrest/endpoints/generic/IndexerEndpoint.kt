package net.typedrest.endpoints.generic

import net.typedrest.endpoints.Endpoint

/**
 * Endpoint that addresses child [TElementEndpoint]s by ID.
 *
 * @param TElementEndpoint The type of [Endpoint] to provide for individual elements.
 */
interface IndexerEndpoint<out TElementEndpoint : Endpoint> : Endpoint {
    /**
     * Returns an element endpoint for a specific child element.
     *
     * @param id The ID identifying the entity.
     */
    operator fun get(id: String): TElementEndpoint
}
