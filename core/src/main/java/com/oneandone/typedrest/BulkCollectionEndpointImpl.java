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
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public BulkCollectionEndpointImpl(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    /**
     * Creates a new paged collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically. Prefix <code>./</code> to append a trailing slash to the
     * parent URI if missing.
     * @param entityType The type of entity the endpoint represents.
     */
    public BulkCollectionEndpointImpl(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    @Override
    protected ElementEndpoint<TEntity> buildElementEndpoint(URI relativeUri) {
        return new ElementEndpointImpl<>(this, relativeUri, entityType);
    }
}
