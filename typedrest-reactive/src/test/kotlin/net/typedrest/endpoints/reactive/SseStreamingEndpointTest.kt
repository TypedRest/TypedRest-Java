package net.typedrest.endpoints.reactive

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.errors.ConflictException
import net.typedrest.http.HttpMethod
import net.typedrest.http.HttpStatusCode
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.*

class SseStreamingEndpointTest : AbstractEndpointTest() {
    private val endpoint = SseStreamingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)
        .apply { autoReconnect = false }

    @Test
    fun testGetObservable() {
        server.enqueue(MockResponse().setSseBody(
            sseEvent("""{"id":5,"name":"test1"}""") +
                sseEvent("""{"id":6,"name":"test2"}""") +
                sseEvent("""{"id":7,"name":"test3"}""")
        ))

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
        server.assertRequest(HttpMethod.GET).withHeader("Accept", "text/event-stream")
    }

    @Test
    fun testEventTypeFilter() {
        val endpoint = SseStreamingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java, eventType = "update")
            .apply { autoReconnect = false }

        server.enqueue(MockResponse().setSseBody(
            sseEvent("""{"id":1,"name":"skip"}""", type = "ignored") +
                sseEvent("""{"id":5,"name":"test1"}""", type = "update") +
                sseEvent("""{"id":99,"name":"default"}""") +
                sseEvent("""{"id":6,"name":"test2"}""", type = "update")
        ))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(
            listOf(
                MockEntity(5, "test1"),
                MockEntity(6, "test2")
            ),
            entities
        )
    }

    @Test
    fun testComments() {
        server.enqueue(MockResponse().setSseBody(
            ": keep-alive\n\n" + sseEvent("""{"id":5,"name":"test1"}""")
        ))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(listOf(MockEntity(5, "test1")), entities)
    }

    @Test
    fun testErrorHandling() {
        server.enqueue(MockResponse()
            .setResponseCode(HttpStatusCode.Conflict)
            .setJsonBody("""{"message":"my message"}"""))

        endpoint.getObservable()
            .test()
            .awaitDone(10, TimeUnit.SECONDS)
            .assertError { it is ConflictException && it.message == "my message" }
    }

    @Test
    fun testNoContentCompletes() {
        endpoint.autoReconnect = true
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(emptyList(), entities)
    }

    @Test
    fun testReconnectsWithLastEventId() {
        val endpoint = SseStreamingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)
            .apply { defaultReconnectionInterval = Duration.ZERO }

        server.enqueue(MockResponse().setSseBody(sseEvent("""{"id":5,"name":"test1"}""", id = "42")))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(listOf(MockEntity(5, "test1")), entities)
        assertNull(server.assertRequest(HttpMethod.GET).getHeader("Last-Event-ID"))
        server.assertRequest(HttpMethod.GET).withHeader("Last-Event-ID", "42")
    }

    @Test
    fun testReconnectsOnServerError() {
        val endpoint = SseStreamingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)
            .apply { defaultReconnectionInterval = Duration.ZERO }

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.ServiceUnavailable))
        server.enqueue(MockResponse().setSseBody(sseEvent("""{"id":5,"name":"test1"}""")))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(listOf(MockEntity(5, "test1")), entities)
    }

    private fun sseEvent(data: String, type: String? = null, id: String? = null): String = buildString {
        if (type != null) append("event: ").append(type).append('\n')
        if (id != null) append("id: ").append(id).append('\n')
        append("data: ").append(data).append("\n\n")
    }
}
