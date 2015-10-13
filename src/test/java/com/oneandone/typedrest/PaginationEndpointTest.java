package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.oneandone.typedrest.AbstractEndpointTest.jsonMime;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.LinkedList;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class PaginationEndpointTest extends AbstractEndpointTest {

    private PaginationEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new PaginationEndpointImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testReadAll() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        Collection<MockEntity> expected = asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2"));
        assertThat(endpoint.readAll(), is(equalTo(expected)));
    }

    @Test
    public void testReadPartialOffset() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=1-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 1-1/2")
                        .withBody("[{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readPartial(1l, null);

        Collection<MockEntity> expected = asList(new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(1l)));
        assertThat(response.getTo(), is(equalTo(1l)));
        assertThat(response.getLength(), is(equalTo(2l)));
    }

    @Test
    public void testReadPartialHead() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=0-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 0-1/2")
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readPartial(0l, 1l);

        Collection<MockEntity> expected = asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(0l)));
        assertThat(response.getTo(), is(equalTo(1l)));
        assertThat(response.getLength(), is(equalTo(2l)));
    }

    @Test
    public void testReadPartialTail() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 2-2/*")
                        .withBody("[{\"id\":6,\"name\":\"test2\"}]")));

        PartialResponse<MockEntity> response = endpoint.readPartial(null, 1l);

        Collection<MockEntity> expected = asList(new MockEntity(6, "test2"));
        assertThat(response.getElements(), is(equalTo(expected)));

        assertThat(response.getFrom(), is(equalTo(2l)));
        assertThat(response.getTo(), is(equalTo(2l)));
        assertThat(response.getLength(), is(nullValue()));
    }

    @Test
    public void testException() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("Range", equalTo("elements=5-10"))
                .willReturn(aResponse()
                        .withStatus(SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("{\"message\":\"test\"}")));

        String exceptionMessage = null;
        try {
            endpoint.readPartial(5l, 10l);
        } catch (IndexOutOfBoundsException ex) {
            exceptionMessage = ex.getMessage();
        }

        assertThat(exceptionMessage, is(CoreMatchers.equalTo("test")));
    }
}
