package com.oneandone.typedrest;

import java.net.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s with bulk create and replace support.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class BulkCollectionEndpointImpl<TEntity>
        extends AbstractBulkCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>
        implements BulkCollectionEndpoint<TEntity> {

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public BulkCollectionEndpointImpl(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public BulkCollectionEndpointImpl(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    @Override
    public ElementEndpoint<TEntity> get(URI relativeUri) {
        return new ElementEndpointImpl<>(this, relativeUri, entityType);
    }
}
