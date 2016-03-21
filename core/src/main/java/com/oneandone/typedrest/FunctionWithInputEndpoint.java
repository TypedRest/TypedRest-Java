package com.oneandone.typedrest;

import java.io.*;
import org.apache.http.*;

/**
 * REST endpoint that represents an RPC-like function which takes
 * <code>TEntity</code> as input and returns <code>TResult</code> as output.
 *
 * @param <TEntity> The type of entity the endpoint takes as input.
 * @param <TResult> The type of entity the endpoint returns as output.
 */
public interface FunctionWithInputEndpoint<TEntity, TResult>
        extends TriggerEndpoint {

    /**
     * Returns the type of entity the endpoint takes as input.
     *
     * @return The class type.
     */
    Class<TEntity> getEntityType();

    /**
     * Returns The type of entity the endpoint returns as output.
     *
     * @return The class type.
     */
    Class<TResult> getResultType();

    /**
     * Triggers the function.
     *
     * @param entity The <code>TEntity</code> to post as input.
     * @return The result returned by the server.
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    TResult trigger(TEntity entity)
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;
}
