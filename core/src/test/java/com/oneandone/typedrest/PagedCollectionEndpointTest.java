package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import java.util.Collection;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;
import static com.oneandone.typedrest.AbstractEndpointTest.JSON_MIME;

public class PagedCollectionEndpointTest extends AbstractEndpointTest {

    private PagedCollectionEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new PagedCollectionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testReadAll() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        Collection<MockEntity> expected = asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2"));
        assertThat(endpoint.readAll(), is(equalTo(expected)));
    }

    @Test
    public void testReadRangeOffset() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=1-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 1-1/2")
                        .withBody("[{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readRange(1l, null);

        Collection<MockEntity> expected = asList(new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(1l)));
        assertThat(response.getTo(), is(equalTo(1l)));
        assertThat(response.getLength(), is(equalTo(2l)));
    }

    @Test
    public void testReadRangeHead() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=0-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 0-1/2")
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readRange(0l, 1l);

        Collection<MockEntity> expected = asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(0l)));
        assertThat(response.getTo(), is(equalTo(1l)));
        assertThat(response.getLength(), is(equalTo(2l)));
    }

    @Test
    public void testReadRangeTail() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 2-2/*")
                        .withBody("[{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readRange(null, 1l);

        Collection<MockEntity> expected = asList(new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(2l)));
        assertThat(response.getTo(), is(equalTo(2l)));
        assertThat(response.getLength(), is(nullValue()));
    }

    @Test
    public void testException() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(RANGE, equalTo("elements=5-10"))
                .willReturn(aResponse()
                        .withStatus(SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"message\":\"test\"}")));

        String exceptionMessage = null;
        try {
            endpoint.readRange(5l, 10l);
        } catch (IllegalStateException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertThat(exceptionMessage, is(CoreMatchers.equalTo("test")));
    }
}
