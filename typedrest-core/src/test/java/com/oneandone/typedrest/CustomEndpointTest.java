package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import org.apache.http.client.fluent.Request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.*;

public class CustomEndpointTest extends AbstractEndpointTest {

    private CustomEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CustomEndpoint(entryPoint, "endpoint");
    }

    @Test
    public void testNotifyLinkHeader() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader("Link", "<target1>; rel=notify, <target2>; rel=notify")
                        .withHeader("Link", "<target3>; rel=notify")));

        endpoint.get();

        assertThat(endpoint.getNotifyTargets(), containsInAnyOrder(
                endpoint.getUri().resolve("target1"),
                endpoint.getUri().resolve("target2"),
                endpoint.getUri().resolve("target3")));
    }

    private class CustomEndpoint extends AbstractEndpoint {

        public CustomEndpoint(Endpoint parent, String relativeUri) {
            super(parent, relativeUri);
        }

        public void get() throws Exception {
            execute(Request.Get(uri));
        }
    }
}
