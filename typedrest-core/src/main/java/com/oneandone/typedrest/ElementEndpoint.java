package com.oneandone.typedrest;

import java.io.*;
import java.util.Optional;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * REST endpoint that represents a single entity.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface ElementEndpoint<TEntity>
        extends Endpoint {

    /**
     * Returns the type of entity the endpoint represents.
     *
     * @return The class type.
     */
    Class<TEntity> getEntityType();

    /**
     * Returns the specific <code>TEntity</code>.
     *
     * @return The specific <code>TEntity</code>.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    TEntity read()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;

    /**
     * Shows whether the server has indicated that
     * {@link #update(java.lang.Object)} is currently allowed.
     *
     * Uses cached data from last response if possible. Tries lazy lookup with
     * HTTP OPTIONS if no requests have been performed yet.
     *
     * @return An indicator whether the method is allowed. If the server did not
     * specify anything {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isUpdateAllowed();

    /**
     * Updates the <code>TEntity</code>.
     *
     * @param entity The modified <code>TEntity</code>.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void update(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;

    /**
     * Shows whether the server has indicated that {@link #delete()} is
     * currently allowed.
     *
     * Uses cached data from last response if possible. Tries lazy lookup with
     * HTTP OPTIONS if no requests have been performed yet.
     *
     * @return An indicator whether the method is allowed. If the server did not
     * specify anything {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isDeleteAllowed();

    /**
     * Deletes the <code>TEntity</code>.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    void delete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;
}
