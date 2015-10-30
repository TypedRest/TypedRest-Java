package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.BlobEndpoint;

/**
 * Component operating on a {@link BlobEndpoint}.
 */
public class BlobComponent extends AbstractComponent<BlobEndpoint> {

    /**
     * Creates a new REST blob component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param caption A caption for the blob.
     */
    public BlobComponent(BlobEndpoint endpoint, String caption) {
        super(endpoint);
        setCaption(caption);
    }
}
