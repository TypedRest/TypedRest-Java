package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.oneandone.typedrest.RestEndpointTest.jsonMime;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;

public class RestCollectionTest extends RestEndpointTest {

    private RestCollection<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new RestCollectionImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testReadAll() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader("Accept", equalTo(jsonMime))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", jsonMime)
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        Collection<MockEntity> expected = new LinkedList<>();
        expected.add(new MockEntity(5, "test1"));
        expected.add(new MockEntity(6, "test2"));
        assertThat(endpoint.readAll(), is(equalTo(expected)));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of fixture")
    public void testCreate() throws Exception {
        URI location = URI.create("/endpoint/new");

        stubFor(post(urlEqualTo("/endpoint/"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Location", location.toASCIIString())));

        RestElement<MockEntity> element = endpoint.create(new MockEntity(5, "test"));
        assertThat(element.getUri(), is(equalTo(serverUri.resolve(location))));
    }
}
