package net.typedrest.endpoints.generic

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.errors.ConflictException
import net.typedrest.http.*
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class CollectionEndpointTest : AbstractEndpointTest() {
    private val endpoint = CollectionEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testGetById() {
        assertEquals("/endpoint/x%2Fy", endpoint["x/y"].uri.path)
    }

    @Test
    fun testGetByIdWithLinkHeaderRelative() {
        server.enqueue(
            MockResponse()
                .setJsonBody("[]")
                .addHeader("Link", "<children{?id}>; rel=child; templated=true")
        )

        endpoint.readAll()
        assertEquals(endpoint.uri.resolve("/children?id=1"), endpoint["1"].uri)
    }

    @Test
    fun testGetByIdWithLinkHeaderAbsolute() {
        server.enqueue(
            MockResponse()
                .setJsonBody("[]")
                .addHeader("Link", "<http://localhost/children/{id}>; rel=child; templated=true")
        )

        endpoint.readAll()
        assertEquals("http://localhost/children/1", endpoint["1"].uri.toString())
    }

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
    fun testGetByEntityWithLinkHeaderAbsolute() {
        server.enqueue(
            MockResponse()
                .setJsonBody("[]")
                .addHeader("Link", "<http://localhost/children/{id}>; rel=child; templated=true")
        )

        endpoint.readAll()
        assertEquals("http://localhost/children/1", endpoint[MockEntity(1, "test")].uri.toString())
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
    fun testReadRangeOffset() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.PartialContent)
                .setHeader("Content-Range", "elements 1-1/2")
                .setJsonBody("""[{"id":6,"name":"test2"}]""")
        )

        val response = endpoint.readRange(from = 1, to = null)
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=1-")
        assertEquals(listOf(MockEntity(6, "test2")), response.elements)
        assertEquals(HttpContentRangeHeader(unit = "elements", from = 1, to = 1, length = 2), response.range)
    }

    @Test
    fun testReadRangeHead() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.PartialContent)
                .setHeader("Content-Range", "elements 0-1/2")
                .setJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
        )

        val response = endpoint.readRange(from = 0, to = 1)
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=0-1")
        assertEquals(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")), response.elements)
        assertEquals(HttpContentRangeHeader(unit = "elements", from = 0, to = 1, length = 2), response.range)
    }

    @Test
    fun testReadRangeTail() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.PartialContent)
                .setHeader("Content-Range", "elements 2-2/2")
                .setJsonBody("""[{"id":6,"name":"test2"}]""")
        )

        val response = endpoint.readRange(from = null, to = 1)
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=-1")
        assertEquals(listOf(MockEntity(6, "test2")), response.elements)
        assertEquals(HttpContentRangeHeader(unit = "elements", from = 2, to = 2, length = 2), response.range)
    }

    @Test
    fun testReadRangeException() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.RangeNotSatisfiable)
                .setJsonBody("""{"message":"test"}""")
        )

        var exceptionMessage: String? = null
        try {
            endpoint.readRange(from = 5, to = 10)
        } catch (ex: ConflictException) {
            exceptionMessage = ex.message
        }
        assertEquals("test", exceptionMessage)
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
    fun testCreateLocation() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpStatusCode.Created)
                .setJsonBody("""{"id":5,"name":"test"}""")
                .addHeader("Location", "/endpoint/new")
        )

        val element = endpoint.create(MockEntity(0, "test"))!!
        server.assertRequest(HttpMethod.POST)
            .withJsonBody("""{"id":0,"name":"test"}""")
        assertEquals(MockEntity(5, "test"), element.response)
        assertEquals(endpoint.uri.resolve("/endpoint/new"), element.uri)
    }

    @Test
    fun testCreateNull() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        assertNull(endpoint.create(MockEntity(0, "test")))
        server.assertRequest(HttpMethod.POST)
            .withJsonBody("""{"id":0,"name":"test"}""")
    }

    @Test
    fun testCreateAll() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.Accepted))

        endpoint.createAll(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")))
        server.assertRequest(HttpMethod.PATCH)
            .withJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
    }

    @Test
    fun testSetAll() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.Accepted))

        endpoint.setAll(listOf(MockEntity(5, "test1"), MockEntity(6, "test2")))
        server.assertRequest(HttpMethod.PUT)
            .withJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
    }

    @Test
    fun testSetAllETag() {
        server.enqueue(
            MockResponse()
                .setJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
                .addHeader("ETag", "\"123abc\"")
        )
        val result = endpoint.readAll()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.setAll(result)
        server.assertRequest(HttpMethod.PUT)
            .withJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]""")
            .withHeader("If-Match", "\"123abc\"")
    }
}
