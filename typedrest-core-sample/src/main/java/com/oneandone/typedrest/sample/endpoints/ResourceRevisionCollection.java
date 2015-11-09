package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;

/**
 * REST endpoint that represents the {@link ResourceRevision}s of a
 * {@link Resource}.
 */
public class ResourceRevisionCollection extends AbstractCollectionEndpoint<ResourceRevision, ResourceRevisionElement> {

    /**
     * Represents the latest {@link ResourceRevision} for the {@link Resource}.
     */
    public final ResourceRevisionElement latest = new ResourceRevisionElement(this, "latest");

    public ResourceRevisionCollection(ResourceElement parent) {
        super(parent, "revisions", ResourceRevision.class);
    }

    @Override
    public ResourceRevisionElement get(String key) {
        return new ResourceRevisionElement(this, key);
    }
}
