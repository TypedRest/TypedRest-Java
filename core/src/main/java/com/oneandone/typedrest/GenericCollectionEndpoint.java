package com.oneandone.typedrest;

import java.io.*;
import java.net.URI;
import java.util.Collection;
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
public interface GenericCollectionEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
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
     * collection. May perform network traffic to look up an URI template.
     *
     * @param key The key identifying the entity in the collection.
     *
     * @return An {@link ElementEndpoint} for a specific element of this
     * collection.
     */
    TElementEndpoint get(String key);

    /**
     * Returns a {@link ElementEndpoint} for a specific child element of this
     * collection. May perform network traffic to look up an URI template.
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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    Collection<TEntity> readAll()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that
     * {@link #create(java.lang.Object)} is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
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

    /**
     * Updates an existing element in the collection.
     *
     * This is a convenience method equivalent to combining
     * {@link #get(java.lang.Object)} with
     * {@link ElementEndpoint#update(java.lang.Object)}.
     *
     * @param element The element to be updated.
     * @return The <code>TEntity</code> as returned by the server, possibly with
     * additional fields set. <code>null</code> if the server does not respond
     * with a result entity.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link ElementEndpoint#read()}. Your changes were rejected
     * to prevent a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    default TEntity update(TEntity element)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        return get(element).update(element);
    }

    /**
     * Deletes an existing element from the collection.
     *
     * This is a convenience method equivalent to combining
     * {@link #get(java.lang.Object)} with {@link ElementEndpoint#delete()}.
     *
     * @param element The element to be deletes.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    default void delete(TEntity element)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        get(element).delete();
    }
}
