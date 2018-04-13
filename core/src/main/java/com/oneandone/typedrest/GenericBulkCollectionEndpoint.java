package com.oneandone.typedrest;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 *
 * @deprecated Use {@link GenericCollectionEndpoint} instead.
 */
@Deprecated
public interface GenericBulkCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends GenericCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates multiple new <code>TEntity</code>s.
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
     *
     * @deprecated Use
     * {@link GenericCollectionEndpoint#createAll(java.lang.Iterable)} instead.
     */
    default void create(Iterable<TEntity> entities)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        createAll(entities);
    }
}
