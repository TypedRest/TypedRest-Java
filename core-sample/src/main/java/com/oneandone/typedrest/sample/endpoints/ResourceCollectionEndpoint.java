package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * REST endpoint that represents the set of {@link Resource}s.
 */
public class ResourceCollectionEndpoint extends AbstractCollectionEndpoint<Resource, ResourceEndpoint> {

    public ResourceCollectionEndpoint(Endpoint parent) {
        super(parent, parent.link("resources"), Resource.class);
    }

    @Override
    public ResourceEndpoint get(URI relativeUri) {
        return new ResourceEndpoint(this, relativeUri);
    }
}
