package net.typedrest.endpoints

import net.typedrest.MockEntity
import net.typedrest.endpoints.generic.ElementEndpointImpl
import net.typedrest.endpoints.rpc.ProducerEndpointImpl
import net.typedrest.http.HttpMethod
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

/**
 * Ensures the serializer handles generic entity types.
 */
class GenericTypeTest : AbstractEndpointTest() {
    private val entities = listOf(MockEntity(5, "test1"), MockEntity(6, "test2"))

    @Test
    fun testRead() {
        val endpoint = ElementEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setXmlListBody(entities))

        assertEquals(entities, endpoint.read())
    }

    @Test
    fun testSet() {
        val endpoint = ElementEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse())

        endpoint.set(entities)
        server.assertRequest(HttpMethod.PUT).withXmlListBody(entities)
    }

    @Test
    fun testProducer() {
        val endpoint = ProducerEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setXmlListBody(entities))

        assertEquals(entities, endpoint.invoke())
    }
}