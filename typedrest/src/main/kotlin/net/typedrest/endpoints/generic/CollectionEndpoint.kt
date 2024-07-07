package net.typedrest.endpoints.generic

/**
 * Endpoint for a collection of [TEntity]s addressable as [ElementEndpoint]s.
 *
 * Use [GenericCollectionEndpoint] instead if you wish to customize the element endpoint type.
 *
 * @param TEntity The type of individual elements in the collection.
 */
interface CollectionEndpoint<TEntity> : GenericCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>
