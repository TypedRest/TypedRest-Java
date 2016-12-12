package com.oneandone.typedrest;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s with pagination support.
 *
 * Use the more constrained {@link PagedCollectionEndpoint} when possible.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 *
 * @deprecated Use {@link CollectionEndpoint} instead.
 */
@Deprecated
public interface GenericPagedCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends GenericCollectionEndpoint<TEntity, TElementEndpoint> {

}
