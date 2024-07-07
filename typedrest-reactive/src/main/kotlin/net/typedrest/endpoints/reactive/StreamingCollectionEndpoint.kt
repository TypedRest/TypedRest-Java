package net.typedrest.endpoints.reactive

import net.typedrest.endpoints.generic.ElementEndpoint

/**
 * Endpoint for a collection of [TEntity]s observable as an append-only stream.
 *
 * Use [GenericStreamingCollectionEndpoint] instead if you wish to customize the element endpoint type.
 *
 * @param TEntity The type of entity the endpoint represents.
 */
interface StreamingCollectionEndpoint<TEntity : Any>
    : GenericStreamingCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>
