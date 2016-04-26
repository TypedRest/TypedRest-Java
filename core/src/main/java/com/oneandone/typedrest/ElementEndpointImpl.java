package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import java.util.Optional;
import lombok.Getter;
import org.apache.http.*;
import static org.apache.http.HttpHeaders.IF_NONE_MATCH;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;

/**
 * REST endpoint that represents a single <code>TEntity</code>.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public class ElementEndpointImpl<TEntity>
        extends AbstractEndpoint implements ElementEndpoint<TEntity> {

    @Getter
    private final Class<TEntity> entityType;

    /**
     * Creates a new element endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param entityType The type of entity the endpoint represents.
     */
    public ElementEndpointImpl(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri);
        this.entityType = entityType;
    }

    /**
     * Creates a new element endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * @param entityType The type of entity the endpoint represents.
     * <code>parent</code>'s.
     */
    public ElementEndpointImpl(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri);
        this.entityType = entityType;
    }

    /**
     * The last entity tag returned by the server. Used for caching and to avoid
     * lost updates.
     */
    private String etag;

    /**
     * The last entity returned by the server. Used for caching.
     */
    private TEntity cachedResponse;

    @Override
    public TEntity read()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        Request request = Request.Get(uri);
        if (etag != null) {
            request = request.addHeader(IF_NONE_MATCH, etag);
        }

        HttpResponse response = execute(request);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_MODIFIED && cachedResponse != null) {
            return cachedResponse;
        }

        handleResponse(response, request);
        Header etagHeader = response.getLastHeader(HttpHeaders.ETAG);
        etag = (etagHeader == null) ? null : etagHeader.getValue();
        return cachedResponse = json.readValue(EntityUtils.toString(response.getEntity()), entityType);
    }

    @Override
    public Optional<Boolean> isUpdateAllowed() {
        return isVerbAllowed("PUT");
    }

    @Override
    public TEntity update(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null.");
        }

        String jsonSend = json.writeValueAsString(entity);
        Request request = Request.Put(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON);
        if (etag != null) {
            request.addHeader(HttpHeaders.IF_MATCH, etag);
        }
        HttpResponse response = executeAndHandle(request);

        return (response.getEntity() == null)
                ? null
                : json.readValue(EntityUtils.toString(response.getEntity()), entityType);
    }

    @Override
    public Optional<Boolean> isDeleteAllowed() {
        return isVerbAllowed("DELETE");
    }

    @Override
    public void delete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        executeAndHandle(Request.Delete(uri));
    }
}
