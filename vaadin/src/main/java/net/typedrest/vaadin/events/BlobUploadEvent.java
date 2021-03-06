package net.typedrest.vaadin.events;

import net.typedrest.BlobEndpoint;

/**
 * Indicates that an upload method on {@link BlobEndpoint} was called.
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
