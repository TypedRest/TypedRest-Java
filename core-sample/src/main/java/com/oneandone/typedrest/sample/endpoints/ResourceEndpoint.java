package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents a {@link Resource}.
 */
public class ResourceEndpoint extends ElementEndpointImpl<Resource> {

    public ResourceEndpoint(ResourceCollectionEndpoint parent, URI relativeUri) {
        super(parent, relativeUri, Resource.class);
    }

    /**
     * Represents the {@link ResourceRevision}s.
     *
     * @return An endpoint.
     */
    public ResourceRevisionCollectionEndpoint getRevisions() {
        return new ResourceRevisionCollectionEndpoint(this);
    }

    /**
     * Exposes all {@link LogEvent}s that relate to this resource.
     *
     * @return An endpoint.
     */
    public StreamEndpoint<LogEvent> getEvents() {
        return new StreamEndpointImpl<>(this, link("events"), LogEvent.class);
    }

    /**
     * Used to resolve {@link Resource#dependencies}.
     *
     * @return An endpoint.
     */
    public ResourceCollectionEndpoint getResources() {
        return new ResourceCollectionEndpoint(this);
    }
}
