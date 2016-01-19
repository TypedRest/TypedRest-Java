package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.*;
import static org.junit.Assert.*;

public class BlobEndpointTest extends AbstractEndpointTest {

    private BlobEndpoint endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new BlobEndpointImpl(entryPoint, "endpoint");
    }

    @Test
    public void testProbe() throws Exception {
        stubFor(options(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Allow", "PUT")));

        endpoint.probe();

        assertThat(endpoint.isDownloadAllowed().get(), is(false));
        assertThat(endpoint.isUploadAllowed().get(), is(true));
    }

    @Test
    public void testDownload() throws Exception {
        byte[] data = {1, 2, 3};

        stubFor(get(urlEqualTo("/endpoint"))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader("Content-Type", "mock/type")
                        .withBody(data)));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        String mimeType = endpoint.downloadTo(stream);

        assertEquals("mock/type", mimeType);
        assertArrayEquals(data, stream.toByteArray());
    }

    @Test
    @Ignore("Works in isolation but fails when executed as part of test suite")
    public void testUpload() throws Exception {
        stubFor(put(urlEqualTo("/endpoint")).withHeader("Content-Type", matching("mock/type"))
                .willReturn(aResponse()
                        .withStatus(SC_NO_CONTENT)));

        File file = File.createTempFile("unit-test", ".tmp");
        endpoint.uploadFrom(file, "mock/type");
    }
}
