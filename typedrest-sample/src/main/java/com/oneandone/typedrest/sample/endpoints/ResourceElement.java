package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents a {@link Resource}.
 */
public class ResourceElement extends ElementEndpointImpl<Resource> {

    /**
     * Represents the {@link ResourceRevision}s.
     */
    public final ResourceRevisionCollection revisions = new ResourceRevisionCollection(this);

    /**
     * Exposes all {@link Event}s that relate to this resource.
     */
    public final StreamEndpointImpl<Event> events = new StreamEndpointImpl<>(this, "events", Event.class);

    public ResourceElement(Endpoint parent, URI relativeUri) {
        super(parent, relativeUri, Resource.class);
    }

    public ResourceElement(Endpoint parent, String relativeUri) {
        super(parent, relativeUri, Resource.class);
    }
}
