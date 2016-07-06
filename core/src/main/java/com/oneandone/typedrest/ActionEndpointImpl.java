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
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s.
     */
    public ActionEndpointImpl(Endpoint referrer, URI relativeUri) {
        super(referrer, relativeUri);
    }

    /**
     * Creates a new action endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Prefix <code>./</code> to append a trailing
     * slash to the <c>referrer</c> URI if missing.
     */
    public ActionEndpointImpl(Endpoint referrer, String relativeUri) {
        super(referrer, relativeUri);
    }

    @Override
    public void trigger()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        executeAndHandle(Request.Post(uri));
    }
}
