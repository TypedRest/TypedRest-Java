package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;

/**
 * REST endpoint that represents the set of {@link Resource}s.
 */
public class ResourceCollection extends AbstractCollectionEndpoint<Resource, ResourceElement> {

    public ResourceCollection(SampleEntryEndpoint parent) {
        super(parent, "resources", Resource.class);
    }

    @Override
    public ResourceElement get(String key) {
        return new ResourceElement(this, key);
    }
}
