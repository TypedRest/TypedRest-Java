package com.oneandone.typedrest;

import java.io.*;
import org.apache.http.*;

/**
 * REST endpoint that represents an RPC-like function which returns
 * <code>TResult</code> as output.
 *
 * @param <TResult> The type of entity the endpoint returns as output.
 */
public interface SupplierEndpoint<TResult>
        extends TriggerEndpoint {

    /**
     * Returns The type of entity the endpoint returns as output.
     *
     * @return The class type.
     */
    Class<TResult> getResultType();

    /**
     * Triggers the function.
     *
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
    TResult trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

}
