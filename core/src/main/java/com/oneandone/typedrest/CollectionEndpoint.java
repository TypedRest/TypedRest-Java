package com.oneandone.typedrest;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface CollectionEndpoint<TEntity>
        extends GenericCollectionEndpoint<TEntity, ElementEndpoint<TEntity>> {
}
