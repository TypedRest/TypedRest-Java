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

    public ResourceRevisionElement(Endpoint parent, String relativeUri) {
        super(parent, relativeUri, ResourceRevision.class);
    }

    /**
     * Promotes the {@link ResourceRevision} to the next stage.
     */
    public TriggerEndpoint promote = new TriggerEndpointImpl(this, "promote");

    /**
     * Represents the blob/file backing the {@link ResourceRevision}.
     */
    public final BlobEndpoint blob = new BlobEndpointImpl(this, "blob");
}
