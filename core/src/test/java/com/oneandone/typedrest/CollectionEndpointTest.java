package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.net.URI;
import static java.util.Arrays.asList;
import java.util.Collection;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.junit.*;
import static com.oneandone.typedrest.AbstractEndpointTest.JSON_MIME;

public class CollectionEndpointTest extends AbstractEndpointTest {

    private CollectionEndpointImpl<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CollectionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
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
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testCreate() throws Exception {
        URI location = URI.create("/endpoint/new");

        stubFor(post(urlEqualTo("/endpoint/"))
                .withRequestBody(equalToJson("{\"id\":5,\"name\":\"test\"}"))
                .willReturn(aResponse()
                        .withStatus(SC_CREATED)
                        .withHeader(LOCATION, location.toASCIIString())));

        ElementEndpoint<MockEntity> element = endpoint.create(new MockEntity(5, "test"));
        assertThat(element.getUri(), is(equalTo(serverUri.resolve(location))));
    }

    @Test
    public void testGetByRelativeUri() {
        assertThat(endpoint.get(URI.create("1")).getUri(),
                is(equalTo(endpoint.getUri().resolve("1"))));
    }

    @Test
    public void testGetByEntity() {
        assertThat(endpoint.get(new MockEntity(1, "test")).getUri(),
                is(equalTo(endpoint.getUri().resolve("1"))));
    }

    @Test
    public void testGetByEntityWithLinkHeaderRelative() throws Exception {
        endpoint.setChildTemplateRel("child");

        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(LINK, "<children/{id}>; rel=child; templated=true")
                        .withBody("[]")));

        endpoint.readAll();

        assertThat(endpoint.get(new MockEntity(1, "test")).getUri(),
                is(equalTo(endpoint.getUri().resolve("children/1"))));
    }

    @Test
    public void testGetByEntityWithLinkHeaderAbsolute() throws Exception {
        endpoint.setChildTemplateRel("child");

        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(LINK, "<" + endpoint.getUri() + "children/{id}>; rel=child; templated=true")
                        .withBody("[]")));

        endpoint.readAll();

        assertThat(endpoint.get(new MockEntity(1, "test")).getUri(),
                is(equalTo(endpoint.getUri().resolve("children/1"))));
    }
}
