package com.oneandone.typedrest;

import com.damnhandy.uri.template.UriTemplate;
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
public abstract class AbstractCollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractEndpoint implements GenericCollectionEndpoint<TEntity, TElementEndpoint> {

    @Getter
    protected final Class<TEntity> entityType;

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
        this.keyProperty = getPropertiesWithAnnotation(entityType, Id.class).stream().findFirst();
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

    /**
     * The Link relation type used by the server to set the collection child
     * element URI template.
     */
    @Getter
    @Setter
    private String childTemplateRel = "child";

    @Override
    public TElementEndpoint get(TEntity entity) {
        String id = getCollectionKey(entity);
        UriTemplate template = linkTemplate(childTemplateRel);

        String href = (template == null)
                ? id
                : template.set("id", getCollectionKey(entity)).expand();
        return get(uri.resolve(href));
    }

    private final Optional<PropertyDescriptor> keyProperty;

    /**
     * Maps a <code>TEntity</code> to a key usable by
     * {@link GenericCollectionEndpoint#get(java.net.URI)}.
     *
     * @param entity The entity to get the key for.
     * @return The key.
     */
    protected String getCollectionKey(TEntity entity) {
        try {
            return keyProperty
                    .orElseThrow(() -> new IllegalStateException(entityType.getSimpleName() + " has no property marked with @Id annotation."))
                    .getReadMethod().invoke(entity).toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        HttpResponse response = executeAndHandle(Request.Get(uri));

        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, entityType);
        return json.readValue(EntityUtils.toString(response.getEntity()), collectionType);
    }

    @Override
    public Optional<Boolean> isCreateAllowed() {
        return isVerbAllowed("POST");
    }

    @Override
    public TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        String jsonSend = json.writeValueAsString(entity);
        HttpResponse response = executeAndHandle(Request.Post(uri).bodyString(jsonSend, ContentType.APPLICATION_JSON));
        Header locationHeader = response.getFirstHeader(LOCATION);
        return (response.getStatusLine().getStatusCode() == SC_CREATED || response.getStatusLine().getStatusCode() == SC_ACCEPTED) && (locationHeader != null)
                ? get(URI.create(locationHeader.getValue()))
                : null;
    }
}
