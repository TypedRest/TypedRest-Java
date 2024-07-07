package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.MockEntity
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class ProducerEndpointTest : AbstractEndpointTest() {
    private val endpoint = ProducerEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testInvoke() {
        server.enqueue(MockResponse().setJsonBody("""{"id":1,"name":"input"}"""))

        assertEquals(MockEntity(1, "input"), endpoint.invoke())
        server.assertRequest(HttpMethod.POST)
    }
}
