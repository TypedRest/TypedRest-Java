package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import lombok.Getter;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.*;

/**
 * REST endpoint that represents an RPC-like action which takes
 * <code>TEntity</code> as input.
 *
 * @param <TEntity> The type of entity the endpoint takes as input.
 */
public class ConsumerEndpointImpl<TEntity>
        extends AbstractTriggerEndpoint
        implements ConsumerEndpoint<TEntity> {

    @Getter
    private final Class<TEntity> entityType;

    /**
     * Creates a new action endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param entityType The type of entity the endpoint takes as input.
     */
    public ConsumerEndpointImpl(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri);
        this.entityType = entityType;
    }

    /**
     * Creates a new action endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Prefix <code>./</code> to append a trailing slash
     * to the parent URI if missing.
     * @param entityType The type of entity the endpoint takes as input.
     */
    public ConsumerEndpointImpl(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri);
        this.entityType = entityType;
    }

    @Override
    public void trigger(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null.");
        }

        String jsonSend = serializer.writeValueAsString(entity);
        executeAndHandle(Request.Put(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
    }
}
