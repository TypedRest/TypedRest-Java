package net.typedrest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import org.apache.http.client.fluent.Request;

/**
 * Base class for building REST RPC-like endpoints.
 */
public abstract class AbstractTriggerEndpoint
        extends AbstractEndpoint
        implements TriggerEndpoint {

    protected AbstractTriggerEndpoint(Endpoint referrer, URI relativeUri) {
        super(referrer, relativeUri);
    }

    protected AbstractTriggerEndpoint(Endpoint referrer, String relativeUri) {
        super(referrer, relativeUri);
    }

    @Override
    public void probe() throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException {
        executeAndHandle(Request.Options(uri));
    }

    @Override
    public Optional<Boolean> isTriggerAllowed() {
        return isMethodAllowed("POST");
    }
}
