package com.oneandone.typedrest;

import org.junit.*;
import com.github.tomakehurst.wiremock.junit.*;
import java.net.URI;
import org.apache.http.entity.ContentType;

public abstract class RestEndpointTest {

    protected RestEntryPoint entryPoint;
    protected static final String jsonMime = ContentType.APPLICATION_JSON.getMimeType();

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void before() {
        entryPoint = new RestEntryPoint(URI.create("http://localhost:8089/"));
    }
}
