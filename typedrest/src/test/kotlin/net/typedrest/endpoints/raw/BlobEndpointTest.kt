package net.typedrest.endpoints.raw

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import java.io.ByteArrayInputStream
import kotlin.test.*

class BlobEndpointTest : AbstractEndpointTest() {
    private val endpoint = BlobEndpointImpl(entryEndpoint, "endpoint")

    @Test
    fun testProbe() {
        server.enqueue(MockResponse().setHeader("Allow", "PUT"))

        endpoint.probe()

        server.assertRequest(HttpMethod.OPTIONS)
        assertEquals(false, endpoint.isDownloadAllowed)
        assertEquals(true, endpoint.isUploadAllowed)
    }

    @Test
    fun testDownload() {
        val data = byteArrayOf(1, 2, 3)

        server.enqueue(MockResponse().setBody(Buffer().write(data)))

        val downloadedData = endpoint.download().use { it.readAllBytes() }
        server.assertRequest(HttpMethod.GET)
        assertContentEquals(data, downloadedData)
    }

    @Test
    fun testUpload() {
        val data = byteArrayOf(1, 2, 3)

        server.enqueue(MockResponse())

        endpoint.uploadFrom(ByteArrayInputStream(data), mimeType = "mock/type")
        server.assertRequest(HttpMethod.PUT).withBody(data, contentType = "mock/type")
    }
}
