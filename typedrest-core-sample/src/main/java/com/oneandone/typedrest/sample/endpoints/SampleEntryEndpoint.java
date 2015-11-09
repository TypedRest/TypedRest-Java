package com.oneandone.typedrest.sample.endpoints;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.sample.models.*;
import java.net.URI;
import org.apache.http.auth.Credentials;

/**
 * Entry point for sample REST interface.
 */
public class SampleEntryEndpoint extends EntryEndpoint {

    public final ResourceCollection resources = new ResourceCollection(this);
    public final PagedResourceCollection resourcesPaged = new PagedResourceCollection(this);
    public final CollectionEndpointImpl<Target> targets = new CollectionEndpointImpl<>(this, "targets", Target.class);
    public final StreamEndpointImpl<Event> events = new StreamEndpointImpl<>(this, "events", Event.class);

    public SampleEntryEndpoint(URI uri) {
        super(uri);
    }

    public SampleEntryEndpoint(URI uri, Credentials credentials) {
        super(uri, credentials);
    }

    public SampleEntryEndpoint(URI uri, String username, String password) {
        super(uri, username, password);
    }
}
