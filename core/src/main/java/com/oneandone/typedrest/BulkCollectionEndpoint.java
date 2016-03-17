package com.oneandone.typedrest;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s with bulk create and replace support.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface BulkCollectionEndpoint<TEntity>
        extends GenericBulkCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>, CollectionEndpoint<TEntity> {
}
