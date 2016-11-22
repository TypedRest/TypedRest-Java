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
                        .withBody("{\"id\":5,\"name\":\"test\"}")));

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
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("{\"id\":5,\"name\":\"test\"}")));
        MockEntity result1 = endpoint.read();
        assertThat(result1, is(equalTo(new MockEntity(5, "test"))));

        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_NONE_MATCH, equalTo("\"123abc\""))
                .willReturn(aResponse()
                        .withStatus(SC_NOT_MODIFIED)));
        MockEntity result2 = endpoint.read();
        assertThat(result2, is(equalTo(new MockEntity(5, "test"))));

        assertThat("Cache responses, not deserialized objects",
                result2, is(not(sameInstance(result1))));
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
    public void testSetNoResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.set(new MockEntity(5, "test"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testSetResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":5,\"name\":\"testXXX\"}")));

        assertThat(endpoint.set(new MockEntity(5, "test")),
                is(equalTo(new MockEntity(5, "testXXX"))));
    }

    @Test
    public void testSetETag() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("{\"id\":5,\"name\":\"test\"}")));
        MockEntity entity = endpoint.read();

        stubFor(put(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_MATCH, matching("\"123abc\""))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));
        endpoint.set(entity);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testSetWithNullValue() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.set(new MockEntity(5, null));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testModifyNoResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.modify(new MockEntity(5, "test"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testModifyResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":5,\"name\":\"testXXX\"}")));

        assertThat(endpoint.modify(new MockEntity(5, "test")),
                is(equalTo(new MockEntity(5, "testXXX"))));
    }

    @Test
    public void testDelete() throws Exception {
        stubFor(delete(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.delete();
    }

    @Test
    public void testDeleteETag() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("{\"id\":5,\"name\":\"test\"}")));
        endpoint.read();

        stubFor(delete(urlEqualTo("/endpoint"))
                .withHeader(IF_MATCH, matching("\"123abc\""))
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
