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
     * Retrieves a link from an HTTP Link header with a specific relation type.
     * May be cached from a previous request or may be lazily requested.
     *
     * @param rel The relation type of the link to look for.
     * @return The href of the link resolved relative to this endpoint's URI.
     * @throws RuntimeException No link with the specified relation type could
     * be found.
     */
    URI link(String rel);

    /**
     * A set of {@link URI}s of other {@link Endpoint}s that may change to
     * reflect operations performed on this endpoint.
     *
     * @return A set of {@link URI}s of other {@link Endpoint}s.
     */
    Set<URI> getNotifyTargets();
}
