package net.typedrest.endpoints.reactive

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class StreamingEndpointTest : AbstractEndpointTest() {
    private val endpoint = StreamingEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testGetObservable() {
        server.enqueue(MockResponse().setJsonBody("""
            {"id":5,"name":"test1"}
            {"id":6,"name":"test2"}
            {"id":7,"name":"test3"}
        """.trimIndent()))

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
    }
}
