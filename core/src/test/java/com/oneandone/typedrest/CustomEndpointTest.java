package com.oneandone.typedrest;

import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import static org.apache.http.HttpStatus.*;
import static org.apache.http.HttpHeaders.*;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicHeader;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.hamcrest.core.StringContains;
import org.junit.rules.ExpectedException;

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

    @Test
    public void testLinkAbsolute() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<http://localhost/b>; rel=target1")));

        endpoint.get();

        assertThat(endpoint.link("target1"), is(equalTo(URI.create("http://localhost/b"))));
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
    public void testGetLinksWithTitles() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<target1>; rel=child; title=\"Title 1\"")
                        .withHeader(LINK, "<target2>; rel=child")));

        endpoint.get();

        Map<URI, String> expected = new HashMap<>();
        expected.put(endpoint.getUri().resolve("target1"), "Title 1");
        expected.put(endpoint.getUri().resolve("target2"), null);
        assertThat(endpoint.getLinksWithTitles("child").entrySet(), equalTo(
                expected.entrySet()));
    }

    @Test
    public void testGetLinksWithTitlesEscaping() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<target1>; rel=child; title=\"Title,1\", <target2>; rel=child")));

        endpoint.get();

        Map<URI, String> expected = new HashMap<>();
        expected.put(endpoint.getUri().resolve("target1"), "Title,1");
        expected.put(endpoint.getUri().resolve("target2"), null);
        assertThat(endpoint.getLinksWithTitles("child").entrySet(), equalTo(
                expected.entrySet()));
    }

    @Test
    public void testSetDefaultLink() throws Exception {
        endpoint.setDefaultLink("child", new String[] {"target1", "target2"});

        Map<URI, String> expected = new HashMap<>();
        expected.put(endpoint.getUri().resolve("target1"), null);
        expected.put(endpoint.getUri().resolve("target2"), null);
        assertThat(endpoint.getLinksWithTitles("child").entrySet(), equalTo(
                expected.entrySet()));
    }

    @Test
    public void testLinkTemplate() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a{?x}>; rel=child; templated=true")));

        endpoint.get();

        assertThat(endpoint.linkTemplate("child").getTemplate(),
                is(equalTo("a{?x}")));
    }

    @Test
    @Ignore("Full link escaping not implemented yet")
    public void testLinkTemplateEscaping() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<http://localhost/b{?x,y}>; rel=search; templated=true")));

        endpoint.get();

        assertThat(endpoint.linkTemplate("search").getTemplate(),
                is(equalTo("http://localhost/b{?x,y}")));
    }

    @Test
    public void testLinkTemplateResolve() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a{?x}>; rel=child; templated=true")));

        endpoint.get();

        assertThat(endpoint.linkTemplate("child", "x", 1),
                is(equalTo(endpoint.getUri().resolve("a?x=1"))));
    }

    @Test
    public void testLinkTemplateResolveAbsolute() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<http://localhost/b{?x}>; rel=child; templated=true")));

        endpoint.get();

        assertThat(endpoint.linkTemplate("child", "x", 1),
                is(equalTo(URI.create("http://localhost/b?x=1"))));
    }

    @Test(expected = RuntimeException.class)
    public void testLinkTemplateException() throws Exception {
        stubFor(head(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)
                        .withHeader(LINK, "<a>; rel=child; templated=true")));

        endpoint.linkTemplate("child2");
    }

    @Test
    public void testSetDefaultLinkTemplate() throws Exception {
        endpoint.setDefaultLinkTemplate("child", "a");

        assertThat(endpoint.linkTemplate("child").getTemplate(), is(equalTo("a")));
    }

    @Test
    public void testLinkBody() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"links\": {"
                                + "  \"single\": {\"href\": \"a\"},"
                                + "  \"collection\": [{\"href\": \"b\", \"title\": \"Title 1\"},{\"href\": \"c\"},true,{\"something\":false}],"
                                + "  \"template\": {\"href\": \"{id}\",\"templated\": true}"
                                + "}}")));

        endpoint.get();

        assertThat(endpoint.link("single"), is(equalTo(endpoint.getUri().resolve("a"))));
        assertThat(endpoint.getLinks("collection"), containsInAnyOrder(
                endpoint.getUri().resolve("b"),
                endpoint.getUri().resolve("c")));
        assertThat(endpoint.linkTemplate("template").getTemplate(), is(equalTo("{id}")));

        Map<URI, String> expected = new HashMap<>();
        expected.put(endpoint.getUri().resolve("b"), "Title 1");
        expected.put(endpoint.getUri().resolve("c"), null);
        assertThat(endpoint.getLinksWithTitles("collection").entrySet(), equalTo(
                expected.entrySet()));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testErrorHandling() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_CONFLICT)));

        exception.expectMessage(new StringContains(" responded with 409 Conflict"));
        endpoint.get();
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
