package com.oneandone.typedrest;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * {@link ElementEndpoint}s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 */
public interface CollectionEndpoint<TEntity>
        extends GenericCollectionEndpoint<TEntity, ElementEndpoint<TEntity>> {

    /**
     * Determines whether the collection contains a specific entity.
     *
     * @param id The ID identifying the entity in the collection.
     * @return <code>true</code> if the entity currently exists,
     * <code>false</code> if it does not.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws RuntimeException Other non-success status code.
     */
    default boolean contains(String id)
            throws IOException, IllegalAccessException {
        return get(id).exists();
    }

    /**
     * Determines whether the collection contains a specific entity.
     *
     * @param element The element to be checked.
     * @return <code>true</code> if the entity currently exists,
     * <code>false</code> if it does not.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws RuntimeException Other non-success status code.
     */
    default boolean contains(TEntity element)
            throws IOException, IllegalAccessException {
        return get(element).exists();
    }

    /**
     * Sets/replaces an existing element in the collection.
     *
     * @param element The new state of the element.
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
     * retrieved with {@link #read()}. Your changes were rejected to prevent a
     * lost update.
     * @throws RuntimeException Other non-success status code.
     */
    default TEntity set(TEntity element)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        return get(element).set(element);
    }

    /**
     * Modifies an existing element in the collection by merging changes.
     *
     * @param element The <code>TEntity</code> data to merge with the existing
     * element.
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
     * retrieved with {@link #read()}. Your changes were rejected to prevent a
     * lost update.
     * @throws RuntimeException Other non-success status code.
     */
    default TEntity merge(TEntity element)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        return get(element).merge(element);
    }

    /**
     * Deletes an existing element from the collection.
     *
     * @param id The ID identifying the entity in the collection.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link #read()}. Your delete call was rejected to prevent
     * a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    default void delete(String id)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        get(id).delete();
    }

    /**
     * Deletes an existing element from the collection.
     *
     * @param element The element to be deleted.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link #read()}. Your delete call was rejected to prevent
     * a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    default void delete(TEntity element)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        get(element).delete();
    }
}
