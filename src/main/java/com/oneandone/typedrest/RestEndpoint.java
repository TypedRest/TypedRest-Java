package com.oneandone.typedrest;

import java.net.*;
import org.apache.http.client.fluent.*;

/**
 * REST endpoint, i.e. a remote HTTP resource.
 */
public interface RestEndpoint {

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
}
