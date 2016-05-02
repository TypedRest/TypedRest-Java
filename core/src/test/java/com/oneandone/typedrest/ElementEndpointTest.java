package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.*;
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
        endpoint = new ElementEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testRead() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("{\"id\":5,\"name\":\"test\"}")));
        assertThat(endpoint.read(),
                is(equalTo(new MockEntity(5, "test"))));

        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_NONE_MATCH, equalTo("\"123abc\""))
                .willReturn(aResponse()
                        .withStatus(SC_NOT_MODIFIED)));
        assertThat(endpoint.read(),
                is(equalTo(new MockEntity(5, "test"))));

    }

    @Test
    public void testReadCache() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":5,\"name\":\"test\"}")));

        assertThat(endpoint.read(),
                is(equalTo(new MockEntity(5, "test"))));
    }

    @Test
    public void testExistsTrue() throws Exception {
        stubFor(head(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)));

        assertThat(endpoint.exists(), is(true));
    }

    @Test
    public void testExistsFalse() throws Exception {
        stubFor(head(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NOT_FOUND)));

        assertThat(endpoint.exists(), is(false));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpdateNoResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.update(new MockEntity(5, "test"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpdateResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":5,\"name\":\"testXXX\"}")));

        assertThat(endpoint.update(new MockEntity(5, "test")),
                is(equalTo(new MockEntity(5, "testXXX"))));
    }

    @Test
    public void testUpdateEtag() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("{\"id\":5,\"name\":\"test\"}")));
        MockEntity entity = endpoint.read();

        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .withHeader(IF_MATCH, matching("\"123abc\""))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));
        endpoint.update(entity);
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
                        .withHeader(CONTENT_TYPE, JSON_MIME)
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
