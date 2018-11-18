package net.typedrest;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s with pagination support.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 *
 * @deprecated Use {@link CollectionEndpoint} instead.
 */
@Deprecated
public interface PagedCollectionEndpoint<TEntity>
        extends GenericPagedCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>, CollectionEndpoint<TEntity> {
}
