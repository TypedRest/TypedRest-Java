package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.*;

/**
 * REST endpoint that represents a {@link ResourceRevision}.
 */
public class ResourceRevisionElement extends ElementEndpointImpl<ResourceRevision> {

    public ResourceRevisionElement(ResourceRevisionCollection parent, URI relativeUri) {
        super(parent, relativeUri, ResourceRevision.class);
    }

    /**
     * Promotes the {@link ResourceRevision} to the next stage.
     *
     * @return An endpoint.
     */
    public TriggerEndpoint getPromote() {
        return new TriggerEndpointImpl(this, link("promote"));
    }

    /**
     * Represents the blob/file backing the {@link ResourceRevision}.
     *
     * @return An endpoint.
     */
    public BlobEndpoint getBlob() {
        return new BlobEndpointImpl(this, link("blob"));
    }
}
