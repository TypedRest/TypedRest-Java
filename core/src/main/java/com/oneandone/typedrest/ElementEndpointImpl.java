package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import java.util.Optional;
import lombok.Getter;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;

/**
 * REST endpoint that represents a single <code>TEntity</code>.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class ElementEndpointImpl<TEntity>
        extends AbstractETagEndpoint implements ElementEndpoint<TEntity> {

    @Getter
    private final Class<TEntity> entityType;

    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s.
     * @param entityType The type of entity the endpoint represents.
     */
    public ElementEndpointImpl(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri);
        this.entityType = entityType;
    }

    /**
     * Creates a new element endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Prefix <code>./</code> to append a trailing
     * slash to the <code>referrer</code> URI if missing.
     * @param entityType The type of entity the endpoint represents.
     */
    public ElementEndpointImpl(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri);
        this.entityType = entityType;
    }

    @Override
    public TEntity read()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        HttpEntity content = getContent();
        return serializer.readValue(EntityUtils.toString(content), entityType);
    }

    @Override
    public boolean exists()
            throws IOException, IllegalAccessException {
        try {
            executeAndHandle(Request.Head(uri));
        } catch (FileNotFoundException ex) {
            return false;
        }
        return true;
    }

    @Override
    public Optional<Boolean> isSetAllowed() {
        return isVerbAllowed("PUT");
    }

    @Override
    public TEntity set(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null.");
        }

        HttpEntity content = new StringEntity(serializer.writeValueAsString(entity), ContentType.APPLICATION_JSON);
        HttpResponse response = putContent(content);
        return (response.getEntity() == null)
                ? null
                : serializer.readValue(EntityUtils.toString(response.getEntity()), entityType);
    }

    @Override
    public Optional<Boolean> isDeleteAllowed() {
        return isVerbAllowed("DELETE");
    }

    @Override
    public void delete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        deleteContent();
    }
}
