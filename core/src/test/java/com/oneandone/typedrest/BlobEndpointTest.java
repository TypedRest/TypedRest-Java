package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.google.common.base.Charsets;
import static com.google.common.io.ByteStreams.toByteArray;
import java.io.File;
import java.io.InputStream;
import org.apache.http.*;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.apache.http.entity.ContentType;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlobEndpointTest extends AbstractEndpointTest {

    private BlobEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new BlobEndpointImpl(entryEndpoint, "endpoint");
    }

    @Test
    public void testProbe() throws Exception {
        stubFor(options(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(HttpHeaders.ALLOW, "PUT")));

        endpoint.probe();

        assertThat(endpoint.isDownloadAllowed().get(), is(false));
        assertThat(endpoint.isUploadAllowed().get(), is(true));
    }

    @Test
    public void testDownload() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody("unit-test")));

        byte[] downloaded = toByteArray(endpoint.download());

        assertArrayEquals(Charsets.UTF_8.encode("unit-test").array(), downloaded);
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpload() throws Exception {
        stubFor(put(urlEqualTo("/endpoint"))
                .withHeader(CONTENT_TYPE, matching("mock/type"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        File file = File.createTempFile("unit-test", ".tmp");
        endpoint.upload(file, ContentType.create("mock/type"));
    }
}
