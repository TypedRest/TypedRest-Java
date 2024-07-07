package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.MockEntity
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class ConsumerEndpointTest : AbstractEndpointTest() {
    private val endpoint = ConsumerEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testInvoke() {
        server.enqueue(MockResponse())

        endpoint.invoke(MockEntity(1, "input"))
        server.assertRequest(HttpMethod.POST)
            .withJsonBody("""{"id":1,"name":"input"}""")
    }
}
