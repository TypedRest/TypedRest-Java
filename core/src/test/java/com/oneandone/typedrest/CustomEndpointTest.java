package com.oneandone.typedrest;

import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class CustomEndpointTest extends AbstractEndpointTest {

    private CustomEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CustomEndpoint(entryEndpoint, "endpoint");
    }

    @Test
    public void testDefaultHeader() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .withHeader("X-Mock", WireMock.equalTo("mock"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=target1-template")));

        endpoint.get();
    }

    @Test
    public void testAllowHeader() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Allow", "PUT, POST")));

        endpoint.get();

        assertThat(endpoint.isVerbAllowed("PUT").get(), is(true));
        assertThat(endpoint.isVerbAllowed("POST").get(), is(true));
        assertThat(endpoint.isVerbAllowed("DELETE").get(), is(false));
    }

    @Test
    public void testLink() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=target1, <b>; rel=target2")));

        endpoint.get();

        assertThat(endpoint.link("target1"), is(equalTo(endpoint.getUri().resolve("a"))));
        assertThat(endpoint.link("target2"), is(equalTo(endpoint.getUri().resolve("b"))));
    }

    @Test
    public void testLinkLazy() throws Exception {
        stubFor(head(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=target1, <b>; rel=target2")));

        assertThat(endpoint.link("target1"), is(equalTo(endpoint.getUri().resolve("a"))));
        assertThat(endpoint.link("target2"), is(equalTo(endpoint.getUri().resolve("b"))));
    }

    @Test(expected = RuntimeException.class)
    public void testLinkException() throws Exception {
        stubFor(head(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=target1")));

        endpoint.link("target2");
    }

    @Test
    public void testGetLinks() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<target1>; rel=notify, <target2>; rel=notify")
                        .withHeader(LINK, "<target3>; rel=notify")));

        endpoint.get();

        assertThat(endpoint.getLinks("notify"), containsInAnyOrder(
                endpoint.getUri().resolve("target1"),
                endpoint.getUri().resolve("target2"),
                endpoint.getUri().resolve("target3")));
    }

    @Test
    public void testLinkTemplate() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=target1; templated=true")));

        endpoint.get();

        assertThat(endpoint.linkTemplate("target1").getTemplate(), is(equalTo("a")));
        assertThat(endpoint.linkTemplate("target2"), is(nullValue()));
    }

    private class CustomEndpoint extends AbstractEndpoint {

        public CustomEndpoint(Endpoint parent, String relativeUri) {
            super(parent, relativeUri);

            defaultHeaders.add(new BasicHeader("X-Mock", "mock"));
        }

        public void get() throws Exception {
            executeAndHandle(Request.Get(uri));
        }
    }
}