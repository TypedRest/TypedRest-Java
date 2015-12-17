package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents the set of {@link Resource}s.
 */
public class ResourceCollection extends AbstractCollectionEndpoint<Resource, ResourceElement> {

    public ResourceCollection(Endpoint parent) {
        super(parent, parent.link("resources"), Resource.class);
    }

    @Override
    public ResourceElement get(URI relativeUri) {
        return new ResourceElement(this, relativeUri);
    }
}
