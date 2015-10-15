package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.oneandone.typedrest.AbstractEndpointTest.jsonMime;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.LinkedList;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class CollectionEndpointTest extends AbstractEndpointTest {

    private CollectionEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CollectionEndpointImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testReadAll() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
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
    @Ignore("Works in isolation but fails when executed as part of fixture")
    public void testCreate() throws Exception {
        URI location = URI.create("/endpoint/new");

        stubFor(post(urlEqualTo("/endpoint/"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_CREATED)
                        .withHeader("Location", location.toASCIIString())));

        ElementEndpoint<MockEntity> element = endpoint.create(new MockEntity(5, "test"));
        assertThat(element.getUri(), is(equalTo(serverUri.resolve(location))));
    }
}
