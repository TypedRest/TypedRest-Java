package net.typedrest.endpoints.generic

import net.typedrest.endpoints.Endpoint
import java.net.URI

/**
 * Endpoint for a collection of [TEntity]s addressable as [ElementEndpoint]s.
 *
 * Use [GenericCollectionEndpointImpl] instead if you wish to customize the element endpoint type.
 *
 * @param TEntity The type of individual elements in the collection.
 */
open class CollectionEndpointImpl<TEntity> : GenericCollectionEndpointImpl<TEntity, ElementEndpoint<TEntity>>, CollectionEndpoint<TEntity> {
    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param entityType The type of individual elements in the collection.
     */
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Class<TEntity>)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl(ref, uri, entityType) })

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the collection.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl(ref, uri, entityType) })
}
