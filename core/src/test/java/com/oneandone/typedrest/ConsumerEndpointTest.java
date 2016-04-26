package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;

public class ConsumerEndpointTest extends AbstractEndpointTest {

    private ConsumerEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new ConsumerEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testTrigger() throws Exception {
        stubFor(post(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":1,\"name\":\"input\"}"))
                .willReturn(aResponse().withStatus(SC_OK)));
 
       endpoint.trigger(new MockEntity(1, "input"));
    }
}
