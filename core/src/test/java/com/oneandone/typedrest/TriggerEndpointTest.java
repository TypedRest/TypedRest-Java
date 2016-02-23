package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TriggerEndpointTest extends AbstractEndpointTest {

    private TriggerEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new TriggerEndpointImpl(entryEndpoint, "endpoint");
    }

    @Test
    public void testProbe() throws Exception {
        stubFor(options(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(ALLOW, "POST")));

        endpoint.probe();
        
        assertThat(endpoint.isTriggerAllowed().get(), is(true));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testTrigger() throws Exception {
        stubFor(post(urlEqualTo("/endpoint"))
                .willReturn(aResponse().withStatus(SC_OK)));

        endpoint.trigger();
    }
}
