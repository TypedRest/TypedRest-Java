package com.oneandone.typedrest;

import static com.oneandone.typedrest.URIUtils.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;
import org.codehaus.jackson.type.*;

/**
 * Base class for building REST endpoints that represents a set of
 * <code>TEntity</code>s as <code>TElement</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElement> The specific type of {@link RestElement}s to provide for individual <code>TEntity</code>s.
 */
public abstract class RestSetBase<TEntity, TElement extends RestElement<TEntity>>
        extends RestEndpointBase implements RestSet<TEntity> {

    protected final Class<TEntity> entityType;

    /**
     * Creates a new element set endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected RestSetBase(RestEndpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, ensureTrailingSlash(relativeUri));
        this.entityType = entityType;
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
    protected RestSetBase(RestEndpoint parent, String relativeUri, Class<TEntity> entityType) {
        // Use this instead of base to ensure trailing slash gets appended for REST Set URIs
        this(parent, URI.create(relativeUri), entityType);
    }

    @Override
    public TElement get(Object id) {
        return getElement(URI.create(id.toString()));
    }

    @Override
    public Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        HttpResponse response = execute(Request.Get(uri));

        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, entityType);
        return json.readValue(EntityUtils.toString(response.getEntity()), collectionType);
    }

    @Override
    public TElement create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        String jsonSend = json.writeValueAsString(entity);
        HttpResponse response = execute(Request.Post(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
        Header locationHeader = response.getFirstHeader("Location");
        return (locationHeader == null) ? null : getElement(URI.create(locationHeader.getValue()));
    }

    /**
     * Instantiates a <code>TElement</code> for an element in this set.
     *
     * @param relativeUri The {@link RestEndpoint#getUri()} of the new
     * <code>TElement</code>.
     * @return The new <code>TEntity</code>.
     */
    protected abstract TElement getElement(URI relativeUri);
}
