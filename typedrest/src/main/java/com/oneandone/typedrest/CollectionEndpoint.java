package com.oneandone.typedrest;

import java.io.*;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public interface CollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends Endpoint {

    /**
     * Returns the type of entity the endpoint represents.
     *
     * @return The class type.
     */
    Class<TEntity> getEntityType();

    /**
     * Returns a {@link ElementEndpoint} for a specific element of this
     * collection. Does not perform any network traffic yet.
     *
     * @param key The key used to identify the element within the collection.
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    TElementEndpoint get(String key);

    /**
     * Returns all <code>TEntity</code>s.
     *
     * @return All <code>TEntity</code>s.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws HttpException Other non-success status code.
     */
    Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException;

    /**
     * Creates a new <code>TEntity</code>.
     *
     * @param entity The new <code>TEntity</code>.
     * @return The newly created <code>TEntity</code>; may be <code>null</code>
     * if the server deferred creating the resource.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws HttpException Other non-success status code.
     */
    TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException;
}
