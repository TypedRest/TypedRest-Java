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

    public final ResourceCollection resources = new ResourceCollection(this);
    public final CollectionEndpointImpl<Target> targets = new CollectionEndpointImpl<>(this, "targets", Target.class);
}
