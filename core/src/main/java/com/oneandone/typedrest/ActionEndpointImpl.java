package com.oneandone.typedrest;

import java.io.*;
import java.net.*;
import org.apache.http.client.fluent.*;

/**
 * REST endpoint that represents a single RPC-like action.
 */
public class ActionEndpointImpl
        extends AbstractTriggerEndpoint
        implements ActionEndpoint {

    /**
     * Creates a new action endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     */
    public ActionEndpointImpl(Endpoint parent, URI relativeUri) {
        super(parent, relativeUri);
    }

    /**
     * Creates a new action endpoint with a relative URI.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Prefix <code>./</code> to append a trailing slash
     * to the parent URI if missing.
     */
    public ActionEndpointImpl(Endpoint parent, String relativeUri) {
        super(parent, relativeUri);
    }

    @Override
    public void trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        executeAndHandle(Request.Post(uri));
    }
}
