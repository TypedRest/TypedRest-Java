package net.typedrest.endpoints.reactive

import net.typedrest.endpoints.Endpoint
import net.typedrest.endpoints.generic.ElementEndpoint
import net.typedrest.endpoints.generic.ElementEndpointImpl
import net.typedrest.javaTypeOf
import java.lang.reflect.Type
import java.net.URI

/**
 * Endpoint for a collection of [TEntity]s observable as an append-only stream using long-polling.
 *
 * Use [GenericStreamingCollectionEndpointImpl] instead if you wish to customize the element endpoint type.
 *
 * @param TEntity The type of individual elements in the collection.
 */
open class StreamingCollectionEndpointImpl<TEntity : Any> : GenericStreamingCollectionEndpointImpl<TEntity, ElementEndpoint<TEntity>>, StreamingCollectionEndpoint<TEntity> {
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
 * Creates a new streaming collection endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TEntity The type of individual elements in the collection. Can be a generic type such as `Event<Contact>`.
 */
inline fun <reified TEntity : Any> StreamingCollectionEndpointImpl(referrer: Endpoint, relativeUri: URI): StreamingCollectionEndpointImpl<TEntity> =
    StreamingCollectionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())

/**
 * Creates a new streaming collection endpoint, taking the entity type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TEntity The type of individual elements in the collection. Can be a generic type such as `Event<Contact>`.
 */
inline fun <reified TEntity : Any> StreamingCollectionEndpointImpl(referrer: Endpoint, relativeUri: String): StreamingCollectionEndpointImpl<TEntity> =
    StreamingCollectionEndpointImpl(referrer, relativeUri, javaTypeOf<TEntity>())
