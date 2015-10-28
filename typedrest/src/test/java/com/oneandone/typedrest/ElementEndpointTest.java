package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class ElementEndpointTest extends AbstractEndpointTest {

    private ElementEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new ElementEndpointImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testRead() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("{\"id\":5,\"name\":\"test\"}")));

        assertThat(endpoint.read(),
                is(equalTo(new MockEntity(5, "test"))));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpdate() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.update(new MockEntity(5, "test"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpdateWithNullValue() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.update(new MockEntity(5, null));
    }

    @Test
    public void testDelete() throws Exception {
        stubFor(delete(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.delete();
    }

    @Test
    public void testException() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_BAD_REQUEST)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("{\"message\":\"test\"}")));

        String exceptionMessage = null;
        try {
            endpoint.read();
        } catch (IllegalArgumentException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertThat(exceptionMessage, is(CoreMatchers.equalTo("test")));
    }
}
