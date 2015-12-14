package com.oneandone.typedrest;

import java.net.*;
import java.util.Set;
import org.apache.http.client.fluent.*;

/**
 * REST endpoint, i.e. a remote HTTP resource.
 */
public interface Endpoint {

    /**
     * The REST executor used to communicate with the remote resource.
     *
     * @return The REST executor used to communicate with the remote resource.
     */
    Executor getRest();

    /**
     * The HTTP URI of the remote resource.
     *
     * @return The HTTP URI of the remote resource.
     */
    URI getUri();

    /**
     * A set of {@link URI}s of other {@link Endpoint}s that may change to
     * reflect operations performed on this endpoint.
     *
     * @return A set of {@link URI}s of other {@link Endpoint}s.
     */
    Set<URI> getNotifyTargets();
}
