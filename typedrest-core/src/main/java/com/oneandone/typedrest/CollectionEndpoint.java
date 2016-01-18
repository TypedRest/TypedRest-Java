package com.oneandone.typedrest;

import java.io.*;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
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
     * Returns a {@link ElementEndpoint} for a specific child element of this
     * collection. Does not perform any network traffic yet.
     *
     * @param relativeUri The URI of the child endpoint relative to the this
     * endpoint.
     *
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    TElementEndpoint get(URI relativeUri);

    /**
     * Returns a {@link ElementEndpoint} for a specific child element of this
     * collection. Does not perform any network traffic yet.
     *
     * @param entity A previously fetched instance of the entity to retrieve a
     * new state for.
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    TElementEndpoint get(TEntity entity);

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
     * @throws RuntimeException Other non-success status code.
     */
    Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;

    /**
     * Shows whether the server has indicated that
     * {@link #create(java.lang.Object)} is currently allowed.
     *
     * Uses cached data from last response if possible. Tries lazy lookup with
     * HTTP OPTIONS if no requests have been performed yet.
     *
     * @return An indicator whether the method is allowed. If the server did not
     * specify anything {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isCreateAllowed();

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
     * @throws RuntimeException Other non-success status code.
     */
    TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException;
}
