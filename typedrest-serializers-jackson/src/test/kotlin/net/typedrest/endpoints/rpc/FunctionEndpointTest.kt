package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.MockEntity
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class FunctionEndpointTest : AbstractEndpointTest() {
    private val endpoint = FunctionEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java, MockEntity::class.java)

    @Test
    fun testInvoke() {
        server.enqueue(MockResponse().setJsonBody("""{"id":2,"name":"input"}"""))

        assertEquals(MockEntity(2, "input"), endpoint.invoke(MockEntity(1, "input")))
        server.assertRequest(HttpMethod.POST)
            .withJsonBody("""{"id":1,"name":"input"}""")
    }
}
