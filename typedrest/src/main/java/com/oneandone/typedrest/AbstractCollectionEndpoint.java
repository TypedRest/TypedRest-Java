package com.oneandone.typedrest;

import static com.oneandone.typedrest.URIUtils.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.entity.*;
import org.apache.http.util.*;
import org.codehaus.jackson.type.*;
import javax.persistence.Id;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractCollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractEndpoint implements CollectionEndpoint<TEntity, TElementEndpoint> {

    @Getter
    protected final Class<TEntity> entityType;

    private final Optional<Method> keyGetMethod;

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractCollectionEndpoint(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, ensureTrailingSlash(relativeUri));
        this.entityType = entityType;

        keyGetMethod = Arrays.stream(entityType.getMethods())
                .filter(x -> x.getAnnotation(Id.class) != null).findFirst();
    }

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractCollectionEndpoint(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        // Use this instead of base to ensure trailing slash gets appended for REST collection URIs
        this(parent, URI.create(relativeUri), entityType);
    }

    @Override
    public TElementEndpoint get(TEntity entity) {
        return get(getCollectionKey(entity));
    }

    /**
     * Maps a <code>TEntity</code> to a key usable by
     * {@link CollectionEndpoint#get(java.lang.String)}.
     *
     * @param entity The entity to get the key for.
     * @return The key.
     */
    protected String getCollectionKey(TEntity entity) {
        try {
            return keyGetMethod
                    .orElseThrow(() -> new IllegalStateException(entityType.getSimpleName() + " has no getter marked with [Key] attribute."))
                    .invoke(entity).toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        HttpResponse response = execute(Request.Get(uri));

        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, entityType);
        return json.readValue(EntityUtils.toString(response.getEntity()), collectionType);
    }

    @Override
    public TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        String jsonSend = json.writeValueAsString(entity);
        HttpResponse response = execute(Request.Post(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
        Header locationHeader = response.getFirstHeader("Location");
        return (locationHeader == null) ? null : get(locationHeader.getValue());
    }
}
