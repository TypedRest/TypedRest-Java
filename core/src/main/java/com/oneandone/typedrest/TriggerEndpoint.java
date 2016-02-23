package com.oneandone.typedrest;

import java.io.*;
import java.util.Optional;
import org.apache.http.*;

/**
 * REST endpoint that represents a single triggerable action.
 */
public interface TriggerEndpoint extends Endpoint {

    /**
     * Queries the server about capabilities of the endpoint without performing
     * any action.
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
    void probe()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;

    /**
     * Shows whether the server has indicated that {@link #trigger()} is
     * currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been
     * sent yet or the server did not specify allowed verbs
     * {@link Optional#empty()} is returned.
     */
    Optional<Boolean> isTriggerAllowed();

    /**
     * Triggers the action.
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
    void trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException;
}
