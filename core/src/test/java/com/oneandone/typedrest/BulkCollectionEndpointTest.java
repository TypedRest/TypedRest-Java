package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;
import static com.oneandone.typedrest.AbstractEndpointTest.JSON_MIME;
import java.util.List;

public class BulkCollectionEndpointTest extends AbstractEndpointTest {

    private BulkCollectionEndpoint<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new BulkCollectionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
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
    public void testSetAllETag() throws Exception {        
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(ETAG, "\"123abc\"")
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));
        List<MockEntity> result = endpoint.readAll();
        
        stubFor(put(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(IF_MATCH, matching("\"123abc\""))
                .withRequestBody(equalToJson("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));
        endpoint.setAll(result);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testCreate() throws Exception {
        stubFor(post(urlEqualTo("/endpoint/bulk"))
                .withRequestBody(equalToJson("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]"))
                .willReturn(aResponse()
                        .withStatus(SC_ACCEPTED)));

        endpoint.create(asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2")));
    }
}
