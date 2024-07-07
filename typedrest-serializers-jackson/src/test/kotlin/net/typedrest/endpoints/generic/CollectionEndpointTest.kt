package net.typedrest.endpoints.generic

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.MockEntity
import net.typedrest.http.*
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class CollectionEndpointTest : AbstractEndpointTest() {
    private val endpoint = CollectionEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testGetByEntityWithLinkHeaderRelative() {
        server.enqueue(
            MockResponse()
                .setJsonBody("[]")
                .addHeader("Link", "<children/{id}>; rel=child; templated=true")
        )

        endpoint.readAll()
        assertEquals(endpoint.uri.resolve("/children/1"), endpoint[MockEntity(1, "test")].uri)
    }

    @Test
    fun testReadAll() {
        server.enqueue(
            MockResponse()
                .setJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
        )

        assertEquals(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")), endpoint.readAll())
    }

    @Test
    fun testReadAllCache() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
        )

        val result1 = endpoint.readAll()
        server.assertRequest(HttpMethod.GET)
        assertEquals(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")), result1)

        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.NotModified)
                .setHeader("ETag", "\"123abc\"")
        )
        val result2 = endpoint.readAll()
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"123abc\"")
        assertEquals(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")), result2)
        assertNotSame(result1, result2, message = "Should cache responses, not deserialized objects")
    }

    @Test
    fun testCreate() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.Created)
                .setJsonBody("""{"id":5,"name":"test"}""")
        )

        val element = endpoint.create(MockEntity(0, "test"))!!
        server.assertRequest(HttpMethod.POST)
            .withJsonBody("""{"id":0,"name":"test"}""")
        assertEquals(MockEntity(5, "test"), element.response)
        assertEquals(endpoint.uri.resolve("/endpoint/5"), element.uri)
    }

    @Test
    fun testCreateAll() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.Accepted))

        endpoint.createAll(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")))
        server.assertRequest(HttpMethod.PATCH)
            .withJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
    }
}
