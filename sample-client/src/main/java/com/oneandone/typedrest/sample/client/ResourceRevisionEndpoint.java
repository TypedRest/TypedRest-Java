package com.oneandone.typedrest.sample.client;

import com.oneandone.typedrest.sample.model.ResourceRevision;
import com.oneandone.typedrest.*;
import java.net.*;

/**
 * REST endpoint that represents a {@link ResourceRevision}.
 */
public class ResourceRevisionEndpoint extends ElementEndpointImpl<ResourceRevision> {

    public ResourceRevisionEndpoint(ResourceRevisionCollectionEndpoint parent, URI relativeUri) {
        super(parent, relativeUri, ResourceRevision.class);
    }

    /**
     * Promotes the {@link ResourceRevision} to the next stage.
     *
     * @return An endpoint.
     */
    public ActionEndpoint getPromote() {
        return new ActionEndpointImpl(this, link("promote"));
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
