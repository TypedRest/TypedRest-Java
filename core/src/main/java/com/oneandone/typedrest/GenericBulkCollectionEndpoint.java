package com.oneandone.typedrest;

import java.io.*;
import java.util.Collection;
import java.util.Optional;
import org.apache.http.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s with bulk create and replace support.
 *
 * Use the more constrained {@link BulkCollectionEndpoint} when possible.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public interface GenericBulkCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends GenericCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Shows whether the server has indicated that
     * {@link #setAll(java.util.Collection)} is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isSetAllAllowed();

    /**
     * Replaces the entire content of the collection with new
     * <code>TEntity</code>s.
     *
     * @param entities >The new set of <code>TEntity</code>s the collection
     * shall contain.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entities have changed since they were
     * last retrieved with {@link #readAll()}. Your changes were rejected to
     * prevent a lost update.
     * @throws RuntimeException Other non-success status code.
     */
    void setAll(Collection<TEntity> entities)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException;

    /**
     * Creates multiple new <code>TEntity</code>s.
     *
     * Uses a link with the relation type <code>bulk</code> to determine the URI
     * to POST to. Defaults to the relative URI <c>bulk</c>.
     *
     * @param entities The new <code>TEntity</code>s.
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
    void create(Iterable<TEntity> entities)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

}
