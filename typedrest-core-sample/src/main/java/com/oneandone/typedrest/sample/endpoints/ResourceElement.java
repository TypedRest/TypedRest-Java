package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents a {@link Resource}.
 */
public class ResourceElement extends ElementEndpointImpl<Resource> {

    public ResourceElement(ResourceCollection parent, URI relativeUri) {
        super(parent, relativeUri, Resource.class);
    }

    /**
     * Represents the {@link ResourceRevision}s.
     *
     * @return An endpoint.
     */
    public ResourceRevisionCollection getRevisions() {
        return new ResourceRevisionCollection(this);
    }

    /**
     * Exposes all {@link LogEvent}s that relate to this resource.
     *
     * @return An endpoint.
     */
    public StreamEndpointImpl<LogEvent> getEvents() {
        return new StreamEndpointImpl<>(this, link("events"), LogEvent.class);
    }

    /**
     * Used to resolve {@link Resource#dependencies}.
     *
     * @return An endpoint.
     */
    public ResourceCollection getResources() {
        return new ResourceCollection(this);
    }
}
