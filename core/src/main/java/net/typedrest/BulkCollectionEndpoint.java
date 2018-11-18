package net.typedrest;

import org.apache.http.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s with bulk create and replace support.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * 
 * @deprecated Use {@link CollectionEndpoint} instead.
 */
@Deprecated
public interface BulkCollectionEndpoint<TEntity>
        extends GenericBulkCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>, CollectionEndpoint<TEntity> {
}
