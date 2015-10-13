package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import javax.naming.OperationNotSupportedException;
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
        extends AbstractEndpoint implements ElementEndpoint<TEntity> {

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

    @Override
    public TEntity read()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        HttpResponse response = execute(Request.Get(uri));
        return json.readValue(EntityUtils.toString(response.getEntity()), entityType);
    }

    @Override
    public void update(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        String jsonSend = json.writeValueAsString(entity);
        execute(Request.Put(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
    }

    @Override
    public void delete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        execute(Request.Delete(uri));
    }
}
