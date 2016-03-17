package com.oneandone.typedrest;

import java.net.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s with pagination support using the HTTP Range header.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class PagedCollectionEndpointImpl<TEntity>
        extends AbstractPagedCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>
        implements PagedCollectionEndpoint<TEntity> {

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public PagedCollectionEndpointImpl(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
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
    public PagedCollectionEndpointImpl(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    @Override
    public ElementEndpoint<TEntity> get(URI relativeUri) {
        return new ElementEndpointImpl<>(this, relativeUri, entityType);
    }
}
