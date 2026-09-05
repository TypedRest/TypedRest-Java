package net.typedrest.endpoints.generic

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.http.*
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class ElementEndpointTest : AbstractEndpointTest() {
    private val endpoint = ElementEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testRead() {
        server.enqueue(
            MockResponse()
                .setXmlBody(MockEntity(5, "test"))
        )

        assertEquals(MockEntity(5, "test"), endpoint.read())
    }

    @Test
    fun testReadCacheETag() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setXmlBody(MockEntity(5, "test"))
        )
        val result1 = endpoint.read()
        server.assertRequest(HttpMethod.GET)
        assertEquals(MockEntity(5, "test"), result1)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        val result2 = endpoint.read()
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"123abc\"")
        assertEquals(MockEntity(5, "test"), result2)
        assertNotSame(result1, result2, message = "Cache responses, not deserialized objects")
    }

    @Test
    fun testSetResult() {
        server.enqueue(MockResponse().setXmlBody(MockEntity(5, "testXXX")))

        assertEquals(MockEntity(5, "testXXX"), endpoint.set(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PUT)
            .withXmlBody(MockEntity(5, "test"))
    }

    @Test
    fun testSetNoResult() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        assertNull(endpoint.set(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PUT)
            .withXmlBody(MockEntity(5, "test"))
    }

    @Test
    fun testSetETag() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setXmlBody(MockEntity(5, "test"))
        )
        val result = endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(result)
        server.assertRequest(HttpMethod.PUT)
            .withHeader("If-Match", "\"123abc\"")
            .withXmlBody(MockEntity(5, "test"))
    }

    @Test
    fun testUpdateRetry() {
        server.enqueue(MockResponse().setXmlBody(MockEntity(5, "test1")).addHeader("ETag", "\"1\""))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.PreconditionFailed))
        server.enqueue(MockResponse().setXmlBody(MockEntity(5, "test2")).addHeader("ETag", "\"2\""))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        endpoint.update({ MockEntity(it.id, "testX") })

        server.assertRequest(HttpMethod.GET)
        server.assertRequest(HttpMethod.PUT).withXmlBody(MockEntity(5, "testX")).withHeader("If-Match", "\"1\"")
        server.assertRequest(HttpMethod.GET)
        server.assertRequest(HttpMethod.PUT).withXmlBody(MockEntity(5, "testX")).withHeader("If-Match", "\"2\"")
    }

    @Test
    fun testMergeResult() {
        server.enqueue(MockResponse().setXmlBody(MockEntity(5, "testXXX")))

        assertEquals(MockEntity(5, "testXXX"), endpoint.merge(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PATCH)
            .withXmlBody(MockEntity(5, "test"))
    }

    @Test
    fun testMergeNoResult() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        assertNull(endpoint.merge(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PATCH)
            .withXmlBody(MockEntity(5, "test"))
    }
}
