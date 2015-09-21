package com.oneandone.typedrest;

import java.io.*;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * REST endpoint that represents a set of <code>TEntity</code>s as
 * <code>TElement</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface RestSet<TEntity>
        extends RestEndpoint {

    /**
     * Returns a <code>TElement</code> for a specific element of this set. Does
     * not perform any network traffic yet.
     *
     * @param id The ID used to identify the element within the set.
     * @return A <code>TElement</code> for a specific element of this set.
     */
    RestElement<TEntity> get(Object id);

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
    RestElement<TEntity> create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException;
}
