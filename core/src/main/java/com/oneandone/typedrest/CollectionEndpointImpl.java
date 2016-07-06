package com.oneandone.typedrest;

import java.net.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class CollectionEndpointImpl<TEntity>
        extends AbstractCollectionEndpoint<TEntity, ElementEndpoint<TEntity>>
        implements CollectionEndpoint<TEntity> {

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public CollectionEndpointImpl(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    /**
     * Creates a new element collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically. Prefix <code>./</code> to append a trailing slash to the
     * parent URI if missing.
     * @param entityType The type of entity the endpoint represents.
     */
    public CollectionEndpointImpl(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    @Override
    public ElementEndpoint<TEntity> get(URI relativeUri) {
        return new ElementEndpointImpl<>(this, relativeUri, entityType);
    }
}
