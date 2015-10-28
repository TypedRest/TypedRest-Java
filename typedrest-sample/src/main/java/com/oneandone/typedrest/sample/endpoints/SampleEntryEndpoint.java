package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;

/**
 * Entry point for sample REST interface.
 */
public class SampleEntryEndpoint extends EntryEndpoint {

    public final CollectionEndpointImpl<PackageEntity> packages = new CollectionEndpointImpl<>(this, "packages", PackageEntity.class);
    public final BlobEndpoint blob = new BlobEndpointImpl(this, "blob");
    public final TriggerEndpoint trigger = new TriggerEndpointImpl(this, "trigger");

    public SampleEntryEndpoint(URI uri) {
        super(uri);
    }
}
