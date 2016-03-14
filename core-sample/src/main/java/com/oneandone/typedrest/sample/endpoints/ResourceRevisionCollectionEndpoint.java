package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents the {@link ResourceRevision}s of a
 * {@link Resource}.
 */
public class ResourceRevisionCollectionEndpoint extends AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionEndpoint> {

    public ResourceRevisionCollectionEndpoint(Endpoint parent) {
        super(parent, parent.link("revisions"), ResourceRevision.class);
    }

    @Override
    public ResourceRevisionEndpoint get(URI relativeUri) {
        return new ResourceRevisionEndpoint(this, relativeUri);
    }

    /**
     * Represents the latest {@link ResourceRevision} for the {@link Resource}.
     *
     * @return An endpoint.
     */
    public ResourceRevisionEndpoint getLatest() {
        return new ResourceRevisionEndpoint(this, link("latest"));
    }
}
