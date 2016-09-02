package com.oneandone.typedrest;

import java.io.*;
import org.apache.http.*;

/**
 * REST endpoint that represents a collection of <code>TEntity</code>s as
 * <code>TElementEndpoint</code>s with pagination support.
 *
 * Use the more constrained {@link PagedCollectionEndpoint} when possible.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public interface GenericPagedCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends GenericCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Returns all <code>TElementEndpoint</code>s within a specific range of the
     * set.
     *
     * @param from The index of the first element to return. <code>null</code>
     * to use <code>to</code> to specify a start point counting from the end of
     * the set.
     * @param to The index of the last element to return. Alternatively the
     * index of the first element to return counting from the end of the set if
     * <code>from</code> is <code>null</code>. <code>null</code> to read to the
     * end.
     * @return A subset of the <code>TElementEndpoint</code>s and the range they
     * come from. May not exactly match the request range.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The requested range is not satisfiable.
     * @throws RuntimeException Other non-success status code.
     */
    PartialResponse<TEntity> readRange(Long from, Long to)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;
}
