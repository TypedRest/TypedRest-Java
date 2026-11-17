package net.typedrest.endpoints

import net.typedrest.MockEntity
import net.typedrest.endpoints.generic.ElementEndpointImpl
import net.typedrest.endpoints.rpc.ProducerEndpointImpl
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

data class Page<T>(val items: List<T>, val total: Int)

/**
 * Ensures the serializer handles generic entity types.
 */
class GenericTypeTest : AbstractEndpointTest() {
    private val pageJson = """{"items":[{"id":5,"name":"test"}],"total":1}"""
    private val page = Page(listOf(MockEntity(5, "test")), total = 1)

    @Test
    fun testRead() {
        val endpoint = ElementEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody(pageJson))

        assertEquals(page, endpoint.read())
    }

    @Test
    fun testSet() {
        val endpoint = ElementEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse())

        endpoint.set(page)
        server.assertRequest(HttpMethod.PUT).withJsonBody(pageJson)
    }

    @Test
    fun testProducerList() {
        val endpoint = ProducerEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody("""[{"id":5,"name":"test"}]"""))

        assertEquals(listOf(MockEntity(5, "test")), endpoint.invoke())
    }
}