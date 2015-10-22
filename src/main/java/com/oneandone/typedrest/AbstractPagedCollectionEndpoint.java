package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;
import org.apache.http.client.fluent.*;
import org.apache.http.util.*;
import org.codehaus.jackson.type.*;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElement</code>s with pagination support using
 * the HTTP Range header.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElement> The specific type of {@link ElementEndpoint}s to provide
 * for individual <code>TEntity</code>s.
 */
public abstract class AbstractPagedCollectionEndpoint<TEntity, TElement extends ElementEndpoint<TEntity>>
        extends AbstractCollectionEndpoint<TEntity, TElement> implements PagedCollectionEndpoint<TEntity> {

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
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, IndexOutOfBoundsException, HttpException {
        String range = RANGE_UNIT + "="
                + (from == null ? "" : from.toString()) + "-"
                + (to == null ? "" : to.toString());

        HttpResponse response = execute(Request.Get(uri).addHeader("Range", range));

        JavaType collectionType = json.getTypeFactory().constructCollectionType(List.class, entityType);
        List<TEntity> elements = json.readValue(EntityUtils.toString(response.getEntity()), collectionType);

        String contentRange = response.getFirstHeader("Content-Range").getValue();
        String[] split = contentRange.split(" ");
        split = split[1].split("/");
        Long contentLength = (split[1].equals("*")) ? null : Long.parseLong(split[1]);
        split = split[0].split("-");
        Long contentFrom = (split[0].isEmpty() ? null : Long.parseLong(split[0]));
        Long contentTo = (split[1].isEmpty() ? null : Long.parseLong(split[1]));

        return new PartialResponse<>(elements, contentFrom, contentTo, contentLength);
    }
}
