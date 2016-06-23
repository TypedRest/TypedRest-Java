package com.oneandone.typedrest;

import com.fasterxml.jackson.databind.JavaType;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.util.*;
import static org.apache.http.HttpHeaders.*;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s with pagination
 * support using the HTTP Range header.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractPagedCollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionEndpoint<TEntity, TElementEndpoint> implements GenericPagedCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates a new paged collection endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractPagedCollectionEndpoint(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
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
    protected AbstractPagedCollectionEndpoint(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    /**
     * The value used for the Range header unit.
     */
    public static final String RANGE_UNIT = "elements";

    @Override
    public PartialResponse<TEntity> readRange(Long from, Long to)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        String range = RANGE_UNIT + "="
                + (from == null ? "" : from.toString()) + "-"
                + (to == null ? "" : to.toString());

        HttpResponse response = executeAndHandle(Request.Get(uri).addHeader(RANGE, range));

        JavaType collectionType = serializer.getTypeFactory().constructCollectionType(List.class, entityType);
        List<TEntity> elements = serializer.readValue(EntityUtils.toString(response.getEntity()), collectionType);

        Header contentRange = response.getFirstHeader(CONTENT_RANGE);
        if (contentRange == null) {
            // Server provided full instead of partial response
            return new PartialResponse<>(elements, 0L, null, null);
        }

        String[] split = contentRange.getValue().split(" ");
        split = split[1].split("/");
        Long contentLength = (split[1].equals("*")) ? null : Long.parseLong(split[1]);
        split = split[0].split("-");
        Long contentFrom = (split[0].isEmpty() ? null : Long.parseLong(split[0]));
        Long contentTo = (split[1].isEmpty() ? null : Long.parseLong(split[1]));

        return new PartialResponse<>(elements, contentFrom, contentTo, contentLength);
    }
}
