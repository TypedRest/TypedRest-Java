package net.typedrest.endpoints.reactive

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import java.time.Duration
import kotlin.test.*

class PollingEndpointTest : AbstractEndpointTest() {
    private val endpoint = PollingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java, endCondition = { it.id == 3L })
        .apply { pollingInterval = Duration.ZERO }

    @Test
    fun testGetObservable() {
        server.enqueue(MockResponse().setJsonBody("""{"id":1,"name":"test"}"""))
        server.enqueue(MockResponse().setJsonBody("""{"id":2,"name":"test"}"""))
        server.enqueue(MockResponse().setJsonBody("""{"id":3,"name":"test"}""").setHeader("Retry-After", "42"))

        val entities = endpoint.getObservable()
            .toList()
            .blockingGet()

        assertEquals(
            listOf(
                MockEntity(1, "test"),
                MockEntity(2, "test"),
                MockEntity(3, "test")
            ),
            entities
        )
        assertEquals(Duration.ofSeconds(42), endpoint.pollingInterval)
    }
}
