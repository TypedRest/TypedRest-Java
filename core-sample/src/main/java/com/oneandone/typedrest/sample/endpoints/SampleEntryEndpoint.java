package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * Entry point for sample REST interface.
 */
public class SampleEntryEndpoint extends EntryEndpoint {

    public SampleEntryEndpoint(URI uri, String username, String password) {
        super(uri, username, password);
    }

    public ResourceCollectionEndpoint getResources() {
        return new ResourceCollectionEndpoint(this);
    }

    public CollectionEndpoint<Target> getTargets() {
        return new CollectionEndpointImpl<>(this, link("targets"), Target.class);
    }
}
