package com.oneandone.typedrest;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import java.io.*;
import java.net.URI;
import java.util.*;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s with bulk create and
 * replace support.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractBulkCollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionEndpoint<TEntity, TElementEndpoint> implements GenericBulkCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates a new bulk collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractBulkCollectionEndpoint(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);

        setDefaultLink("bulk", "bulk");
    }

    /**
     * Creates a new bulk collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractBulkCollectionEndpoint(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);

        setDefaultLink("bulk", "bulk");
    }

    @Override
    public Optional<Boolean> isSetAllAllowed() {
        return isVerbAllowed("PUT");
    }

    @Override
    public void setAll(Collection<TEntity> entities)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entities == null) {
            throw new IllegalArgumentException("entities must not be null.");
        }

        String jsonSend = serializer.writeValueAsString(entities);
        executeAndHandle(Request.Put(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
    }

    @Override
    public void create(Iterable<TEntity> entities)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entities == null) {
            throw new IllegalArgumentException("entities must not be null.");
        }

        String jsonSend = serializer.writeValueAsString(entities);
        executeAndHandle(Request.Post(link("bulk")).bodyString(jsonSend, ContentType.APPLICATION_JSON));
    }
}
