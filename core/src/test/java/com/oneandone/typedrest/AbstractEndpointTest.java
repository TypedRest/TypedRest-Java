package com.oneandone.typedrest;

import org.junit.*;
import com.github.tomakehurst.wiremock.junit.*;
import java.net.URI;
import org.apache.http.entity.ContentType;

public abstract class AbstractEndpointTest {

    protected EntryEndpoint entryEndpoint;
    protected static final String JSON_MIME = ContentType.APPLICATION_JSON.getMimeType();
    protected static final String LINK = "Link";

    private final int port = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);

    protected final URI serverUri = URI.create("http://localhost:" + port + "/");

    @Before
    public void before() {
        entryEndpoint = new EntryEndpoint(serverUri);
    }
}
