package net.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.net.URI;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;

public class EntryEndpointTest {

    private final int port = 8089;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);

    private final URI serverUri = URI.create("http://localhost:" + port + "/");

    @Test
    public void testBasicAuthPreemtive() throws Exception {
        stubFor(get(urlEqualTo("/"))
                .withHeader(AUTHORIZATION, equalTo("Basic dXNlcjpwYXNzd29yZA=="))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody("ok")));

        EntryEndpoint endpoint = new EntryEndpoint(serverUri, "user", "password");
        endpoint.readMeta();
    }

    @Test
    @Ignore("Currently not implemented")
    public void testBasicAuthNegotiated() throws Exception {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)
                        .withHeader(WWW_AUTHENTICATE, "Basic")
                        .withBody("ok")));

        stubFor(get(urlEqualTo("/"))
                .withHeader(AUTHORIZATION, equalTo("Basic dXNlcjpwYXNzd29yZA=="))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody("ok")));

        EntryEndpoint endpoint = new EntryEndpoint(serverUri, "user", "password");
        endpoint.readMeta();
    }

    @Test
    @Ignore("Currently not implemented")
    public void testBasicAuthNegotiatedWithAlternatives() throws Exception {
        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withStatus(SC_UNAUTHORIZED)
                        .withHeader(WWW_AUTHENTICATE, "Basic, Bearer")
                        .withBody("ok")));

        stubFor(get(urlEqualTo("/"))
                .withHeader(AUTHORIZATION, equalTo("Basic dXNlcjpwYXNzd29yZA=="))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody("ok")));

        EntryEndpoint endpoint = new EntryEndpoint(serverUri, "user", "password");
        endpoint.readMeta();
    }
}
