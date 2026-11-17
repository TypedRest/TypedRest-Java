package net.typedrest.endpoints.generic

import net.typedrest.endpoints.Endpoint
import net.typedrest.javaTypeOf
import java.lang.reflect.Type
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
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Type)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl<TEntity>(ref, uri, entityType) })

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the collection.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Type)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl<TEntity>(ref, uri, entityType) })

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param entityType The type of individual elements in the collection.
     */
    constructor(referrer: Endpoint, relativeUri: URI, entityType: Class<TEntity>)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl<TEntity>(ref, uri, entityType) })

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param entityType The type of individual elements in the collection.
     */
    constructor(referrer: Endpoint, relativeUri: String, entityType: Class<TEntity>)
        : super(referrer, relativeUri, entityType, { ref, uri -> ElementEndpointImpl<TEntity>(ref, uri, entityType) })
}

/**
 * Creates a new element collection endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TEntity The type of individual elements in the collection. Can be a generic type such as `Versioned<Contact>`.
 */
inline fun <reified TEntity> CollectionEndpointImpl(referrer: Endpoint, relativeUri: URI): CollectionEndpointImpl<TEntity> =
    CollectionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())

/**
 * Creates a new element collection endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TEntity The type of individual elements in the collection. Can be a generic type such as `Versioned<Contact>`.
 */
inline fun <reified TEntity> CollectionEndpointImpl(referrer: Endpoint, relativeUri: String): CollectionEndpointImpl<TEntity> =
    CollectionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())
