package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.http.HttpMethod
import net.typedrest.tests.assertRequest
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class ActionEndpointTest : AbstractEndpointTest() {
    private val endpoint = ActionEndpointImpl(entryEndpoint, "endpoint")

    @Test
    fun testProbe() {
        server.enqueue(MockResponse().setHeader("Allow", "POST"))

        endpoint.probe()

        server.assertRequest(HttpMethod.OPTIONS)
        assertEquals(true, endpoint.isInvokeAllowed)
    }

    @Test
    fun testInvoke() {
        server.enqueue(MockResponse())

        endpoint.invoke()
        server.assertRequest(HttpMethod.POST)
    }
}
