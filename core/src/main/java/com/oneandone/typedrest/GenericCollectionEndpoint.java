package com.oneandone.typedrest;

import java.io.*;
import java.util.List;
import java.util.Optional;
import org.apache.http.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s.
 *
 * Use the more constrained {@link CollectionEndpoint} when possible.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public interface GenericCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends Endpoint {

    /**
     * Returns the type of entity the endpoint represents.
     *
     * @return The class type.
     */
    Class<TEntity> getEntityType();

    /**
     * Returns a {@link ElementEndpoint} for a specific child element of this
     * collection.
     *
     * @param id The ID identifying the entity in the collection.
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    TElementEndpoint get(String id);

    /**
     * Returns a {@link ElementEndpoint} for a specific child element of this
     * collection.
     *
     * @param entity An existing entity to extract the ID from.
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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    List<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that
     * {@link #create(java.lang.Object)} is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the method is allowed. If no request has
     * been sent yet or the server did not specify allowed methods
     * {@link Optional#empty()} is returned.
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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    TElementEndpoint create(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;
}
