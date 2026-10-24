package net.typedrest.endpoints.raw;

import net.typedrest.endpoints.EntryEndpoint;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures the constructors of raw endpoints can be called from Java without their optional parameters.
 */
class JavaConstructorsTest {
    private final EntryEndpoint entryEndpoint = new EntryEndpoint(URI.create("http://localhost/"));

    @Test
    void uploadEndpoint() {
        assertEquals(URI.create("http://localhost/endpoint"),
            new UploadEndpointImpl(entryEndpoint, "endpoint").getUri());
        assertEquals(URI.create("http://localhost/endpoint"),
            new UploadEndpointImpl(entryEndpoint, URI.create("endpoint")).getUri());
    }
}
