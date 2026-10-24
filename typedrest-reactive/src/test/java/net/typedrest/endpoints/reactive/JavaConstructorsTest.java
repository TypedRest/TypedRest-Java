package net.typedrest.endpoints.reactive;

import net.typedrest.MockEntity;
import net.typedrest.endpoints.EntryEndpoint;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures the constructors of reactive endpoints can be called from Java without their optional parameters.
 */
class JavaConstructorsTest {
    private final EntryEndpoint entryEndpoint = new EntryEndpoint(URI.create("http://localhost/"));

    @Test
    void sseStreamingEndpoint() {
        assertEquals(URI.create("http://localhost/endpoint"),
            new SseStreamingEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class).getUri());
        assertEquals(URI.create("http://localhost/endpoint"),
            new SseStreamingEndpointImpl<>(entryEndpoint, URI.create("endpoint"), MockEntity.class).getUri());
    }

    @Test
    void streamingEndpoint() {
        assertEquals(URI.create("http://localhost/endpoint"),
            new StreamingEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class).getUri());
        assertEquals(URI.create("http://localhost/endpoint"),
            new StreamingEndpointImpl<>(entryEndpoint, URI.create("endpoint"), MockEntity.class).getUri());
    }

    @Test
    void pollingEndpoint() {
        assertEquals(URI.create("http://localhost/endpoint"),
            new PollingEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class).getUri());
        assertEquals(URI.create("http://localhost/endpoint"),
            new PollingEndpointImpl<>(entryEndpoint, URI.create("endpoint"), MockEntity.class).getUri());
    }
}
