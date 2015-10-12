package com.oneandone.typedrest;

import org.junit.*;
import com.github.tomakehurst.wiremock.junit.*;
import java.net.URI;
import org.apache.http.entity.ContentType;

public abstract class RestEndpointTest {

    protected RestEntryPoint entryPoint;
    protected static final String jsonMime = ContentType.APPLICATION_JSON.getMimeType();

    private static final int port = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);

    protected static final URI serverUri = URI.create("http://localhost:" + port + "/");

    @Before
    public void before() {
        entryPoint = new RestEntryPoint(serverUri);
    }
}
