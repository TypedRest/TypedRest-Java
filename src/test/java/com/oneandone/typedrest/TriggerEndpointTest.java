package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.oneandone.typedrest.AbstractEndpointTest.jsonMime;
import static org.apache.http.HttpStatus.*;
import org.junit.*;

public class TriggerEndpointTest extends AbstractEndpointTest {

    private TriggerEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new TriggerEndpointImpl(entryPoint, "endpoint");
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testTrigger() throws Exception {
        stubFor(post(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .willReturn(aResponse().withStatus(SC_OK)));

        endpoint.trigger();
    }
}
