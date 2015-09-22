package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class RestElementTest extends RestEndpointTest {

    private RestElement<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new RestElementImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testRead() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("{\"id\":5,\"name\":\"test\"}")));

        assertThat(endpoint.read(),
                is(equalTo(new MockEntity(5, "test"))));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of fixture")
    public void testUpdate() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(204)));

        endpoint.update(new MockEntity(5, "test"));
    }

    @Test
    public void testDelete() throws Exception {
        stubFor(delete(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(204)));

        endpoint.delete();
    }
}
