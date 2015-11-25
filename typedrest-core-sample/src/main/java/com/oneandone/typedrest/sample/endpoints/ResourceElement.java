package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents a {@link Resource}.
 */
public class ResourceElement extends ElementEndpointImpl<Resource> {

    public ResourceElement(Endpoint parent, URI relativeUri) {
        super(parent, relativeUri, Resource.class);
    }

    public ResourceElement(Endpoint parent, String relativeUri) {
        super(parent, relativeUri, Resource.class);
    }

    /**
     * Represents the {@link ResourceRevision}s.
     */
    public final ResourceRevisionCollection revisions = new ResourceRevisionCollection(this);

    /**
     * Exposes all {@link LogEvent}s that relate to this resource.
     */
    public final StreamEndpointImpl<LogEvent> events = new StreamEndpointImpl<>(this, "events", LogEvent.class);

    /**
     * Used to resolve {@link Resource#dependencies}
     */
    public final ResourceCollection parentResources = new ResourceCollection(this);
}
