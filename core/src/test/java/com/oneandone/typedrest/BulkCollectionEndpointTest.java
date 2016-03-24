package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;
import static com.oneandone.typedrest.AbstractEndpointTest.JSON_MIME;
import java.net.URI;

public class BulkCollectionEndpointTest extends AbstractEndpointTest {

    private BulkCollectionEndpointImpl<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new BulkCollectionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
        endpoint.setBulkCreateSuffix(URI.create("my-bulk"));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testSetAll() throws Exception {
        stubFor(put(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withRequestBody(equalToJson("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        endpoint.setAll(asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2")));
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testCreate() throws Exception {
        stubFor(post(urlEqualTo("/endpoint/my-bulk"))
                .withRequestBody(equalToJson("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]"))
                .willReturn(aResponse()
                        .withStatus(SC_ACCEPTED)));

        endpoint.create(asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2")));
    }
}
