package com.oneandone.typedrest.vaadin.events;

import com.oneandone.typedrest.BlobEndpoint;

/**
 * Indicates that
 * {@link BlobEndpoint#uploadFrom(java.io.File, java.lang.String)} was called.
 */
public class BlobUploadEvent extends EndpointEvent<BlobEndpoint> {

    /**
     * Creates a new blob upload event.
     *
     * @param endpoint The endpoint that data was uploaded to.
     */
    public BlobUploadEvent(BlobEndpoint endpoint) {
        super(endpoint);
    }
}
