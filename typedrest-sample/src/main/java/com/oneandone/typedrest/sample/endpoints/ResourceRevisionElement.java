package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.*;

/**
 * REST endpoint that represents a {@link ResourceRevision}.
 */
public class ResourceRevisionElement extends ElementEndpointImpl<ResourceRevision> {

    /**
     * Promotes the {@link ResourceRevision} to the next {@link Stage}.
     */
    public TriggerEndpoint promote = new TriggerEndpointImpl(this, "promote");

    /**
     * Promotes the {@link ResourceRevision} to the next {@link Stage}.
     * Overrides the restriction that {@link ResourceRevision}s can only be
     * promoted to the next {@link Stage} once they have been tested with
     * {@link DesiredStateWorkingCopyElement#apply}.
     */
    public TriggerEndpoint promoteForce = new TriggerEndpointImpl(this, "promote-force");

    /**
     * Represents the blob/file backing the {@link ResourceRevision}.
     */
    public final BlobEndpoint blob = new BlobEndpointImpl(this, "blob");

    public ResourceRevisionElement(ResourceRevisionCollection parent, URI relativeUri) {
        super(parent, relativeUri, ResourceRevision.class);
    }

    public ResourceRevisionElement(Endpoint parent, String relativeUri) {
        super(parent, relativeUri, ResourceRevision.class);
    }
}
