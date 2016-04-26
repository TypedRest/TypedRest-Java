package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SupplierEndpointTest extends AbstractEndpointTest {

    private SupplierEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new SupplierEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testTrigger() throws Exception {
        stubFor(post(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":2,\"name\":\"result\"}")));

        assertThat(endpoint.trigger(),
                is(equalTo(new MockEntity(2, "result"))));
    }
}
