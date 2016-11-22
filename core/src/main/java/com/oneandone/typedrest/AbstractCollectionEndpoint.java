package com.oneandone.typedrest;

import com.fasterxml.jackson.databind.JavaType;
import static com.oneandone.typedrest.BeanUtils.*;
import static com.oneandone.typedrest.URIUtils.ensureTrailingSlash;
import org.apache.http.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import lombok.*;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends AbstractETagEndpoint implements GenericCollectionEndpoint<TEntity, TElementEndpoint> {

    @Getter
    protected final Class<TEntity> entityType;

    /**
     * Creates a new paged collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractCollectionEndpoint(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, ensureTrailingSlash(relativeUri));
        this.entityType = entityType;
        this.idProperty = getPropertiesWithAnnotation(entityType, Id.class).stream().findFirst();

        setDefaultLinkTemplate("child", "{id}");
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
    protected AbstractCollectionEndpoint(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri.endsWith("/") ? relativeUri : relativeUri + "/");
        this.entityType = entityType;
        this.idProperty = getPropertiesWithAnnotation(entityType, Id.class).stream().findFirst();

        setDefaultLinkTemplate("child", "{id}");
    }

    /**
     * Builds a {@link ElementEndpoint} for a specific child element of this
     * collection. Does not perform any network traffic yet.
     *
     * @param relativeUri The URI of the child endpoint relative to the this
     * endpoint.
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    protected abstract TElementEndpoint buildElementEndpoint(URI relativeUri);

    @Override
    public TElementEndpoint get(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id");
        }

        return buildElementEndpoint(linkTemplate("child", "id", id));
    }

    @Override
    public TElementEndpoint get(TEntity entity) {
        try {
            String id = idProperty
                    .orElseThrow(() -> new IllegalStateException(entityType.getSimpleName() + " has no property marked with @Id annotation."))
                    .getReadMethod().invoke(entity).toString();
            return get(id);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private final Optional<PropertyDescriptor> idProperty;

    @Override
    public List<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        HttpEntity content = getContent();

        JavaType collectionType = serializer.getTypeFactory().constructCollectionType(List.class, entityType);
        return serializer.readValue(EntityUtils.toString(content), collectionType);
    }

    @Override
    public Optional<Boolean> isCreateAllowed() {
        return isMethodAllowed("POST");
    }

    @Override
    public TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null.");
        }

        String jsonSend = serializer.writeValueAsString(entity);
        HttpResponse response = executeAndHandle(Request.Post(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
        Header locationHeader = response.getFirstHeader(LOCATION);
        return (response.getStatusLine().getStatusCode() == SC_CREATED || response.getStatusLine().getStatusCode() == SC_ACCEPTED) && (locationHeader != null)
                ? buildElementEndpoint(URI.create(locationHeader.getValue()))
                : null;
    }
}
