package net.typedrest.endpoints.reactive

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.http.HttpMethod
import net.typedrest.http.HttpStatusCode
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import java.time.Duration
import kotlin.test.*

class StreamingCollectionEndpointTest : AbstractEndpointTest() {
    private val endpoint = StreamingCollectionEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)
        .apply { pollingInterval = Duration.ZERO }

    @Test
    fun testGetObservable() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatusCode.PartialContent)
            .setHeader("Content-Range", "elements 0-1/*")
            .setJsonBody("""[{"id":5,"name":"test1"},{"id":6,"name":"test2"}]"""))
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatusCode.PartialContent)
            .setHeader("Content-Range", "elements 2-2/3")
            .setHeader("Retry-After", "42")
            .setJsonBody("""[{"id":7,"name":"test3"}]"""))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(
            listOf(
                MockEntity(5, "test1"),
                MockEntity(6, "test2"),
                MockEntity(7, "test3")
            ),
            entities
        )
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=0-")
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=2-")
        assertEquals(Duration.ofSeconds(42), endpoint.pollingInterval)
    }

    @Test
    fun testGetObservableOffset() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatusCode.PartialContent)
            .setHeader("Content-Range", "elements 2-2/3")
            .setJsonBody("""[{"id":7,"name":"test3"}]"""))

        val entities = endpoint.getObservable(startIndex = 2)
            .toList()
            .blockingGet()

        assertEquals(listOf(MockEntity(7, "test3")), entities)
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=2-")
    }

    @Test
    fun testGetObservableTail() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatusCode.PartialContent)
            .setHeader("Content-Range", "elements 2-2/3")
            .setJsonBody("""[{"id":7,"name":"test3"}]"""))

        val entities = endpoint.getObservable(startIndex = -1)
            .toList()
            .blockingGet()

        assertEquals(listOf(MockEntity(7, "test3")), entities)
        server.assertRequest(HttpMethod.GET).withHeader("Range", "elements=-1")
    }
}
