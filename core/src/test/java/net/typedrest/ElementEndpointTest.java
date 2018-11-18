package net.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
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
    public void testUpdateRetry() throws Exception {
        stubFor(get(urlEqualTo("/endpoint")).inScenario("testUpdateRetry")
                .whenScenarioStateIs(STARTED)
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"1\"")
                        .withBody("{\"id\":5,\"name\":\"test1\"}"))
                .willSetStateTo("Second attempt"));
        stubFor(put(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_MATCH, matching("\"1\""))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"testX\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_PRECONDITION_FAILED)));
        stubFor(get(urlEqualTo("/endpoint")).inScenario("testUpdateRetry")
                .whenScenarioStateIs("Second attempt")
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"2\"")
                        .withBody("{\"id\":5,\"name\":\"test2\"}")));
        stubFor(put(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_MATCH, matching("\"2\""))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"testX\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.update(x -> x.setName("testX"));
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateFail() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"1\"")
                        .withBody("{\"id\":5,\"name\":\"test1\"}")));
        stubFor(put(urlEqualTo("/endpoint"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_MATCH, matching("\"1\""))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"testX\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_PRECONDITION_FAILED)));

        endpoint.update(x -> x.setName("testX"), 0);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testMergeNoResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.merge(new MockEntity(5, "test"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testMergeResult() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":5,\"name\":\"testXXX\"}")));

        assertThat(endpoint.merge(new MockEntity(5, "test")),
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
