package com.oneandone.typedrest;

/**
 * REST endpoint that represents a stream of <code>TEntity</code>s as
 * {@link ElementEndpoint}s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface StreamEndpoint<TEntity>
        extends GenericStreamEndpoint<TEntity, ElementEndpoint<TEntity>>, CollectionEndpoint<TEntity> {
}
