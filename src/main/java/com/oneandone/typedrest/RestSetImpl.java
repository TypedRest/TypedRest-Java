package com.oneandone.typedrest;

import java.net.*;

/**
 * REST endpoint that represents a set of <code>TEntity</code>s as
 * {@link RestElement}s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class RestSetImpl<TEntity>
        extends RestSetBase<TEntity, RestElement<TEntity>> {

    /**
     * Creates a new element set endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public RestSetImpl(RestEndpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    /**
     * Creates a new element set endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    public RestSetImpl(RestEndpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    @Override
    protected RestElement<TEntity> getElement(URI relativeUri) {
        return new RestElementImpl<>(this, relativeUri, entityType);
    }
}
