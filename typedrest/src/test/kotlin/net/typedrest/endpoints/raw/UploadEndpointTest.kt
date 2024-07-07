package net.typedrest.endpoints.raw

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import java.io.ByteArrayInputStream
import kotlin.test.Test

class UploadEndpointTest : AbstractEndpointTest() {
    @Test
    fun testUploadRaw() {
        val endpoint = UploadEndpointImpl(entryEndpoint, "endpoint")

        val data = byteArrayOf(1, 2, 3)

        server.enqueue(MockResponse())

        endpoint.uploadFrom(ByteArrayInputStream(data), mimeType = "mock/type")
        server.assertRequest(HttpMethod.POST).withBody(data, contentType = "mock/type")
    }

    @Test
    fun testUploadForm() {
        val endpoint = UploadEndpointImpl(entryEndpoint, "endpoint", formField = "data")

        val data = byteArrayOf(1, 2, 3)

        server.enqueue(MockResponse())

        endpoint.uploadFrom(ByteArrayInputStream(data), mimeType = "mock/type", fileName = "file.dat")
        server.assertRequest(HttpMethod.POST)
    }
}
