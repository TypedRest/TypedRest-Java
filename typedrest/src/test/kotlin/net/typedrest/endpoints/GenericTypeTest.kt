package net.typedrest.endpoints

import kotlinx.serialization.Serializable
import net.typedrest.MockEntity
import net.typedrest.endpoints.generic.*
import net.typedrest.endpoints.rpc.*
import net.typedrest.http.HttpMethod
import net.typedrest.http.HttpStatusCode
import net.typedrest.javaTypeOf
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import java.lang.reflect.ParameterizedType
import kotlin.test.*

@Serializable
data class Page<T>(val items: List<T>, val total: Int)

/**
 * Ensures endpoints work with generic entity types passed as reified type arguments.
 */
class GenericTypeTest : AbstractEndpointTest() {
    private val pageJson = """{"items":[{"id":5,"name":"test"}],"total":1}"""
    private val page = Page(listOf(MockEntity(5, "test")), total = 1)

    @Test
    fun testJavaTypeOf() {
        assertEquals(MockEntity::class.java, javaTypeOf<MockEntity>())

        val type = assertIs<ParameterizedType>(javaTypeOf<Page<MockEntity>>())
        assertEquals(Page::class.java, type.rawType)
        assertContentEquals(arrayOf(MockEntity::class.java), type.actualTypeArguments)
    }

    @Test
    fun testElementRead() {
        val endpoint = ElementEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody(pageJson))

        assertEquals(page, endpoint.read())
    }

    @Test
    fun testElementSet() {
        val endpoint = ElementEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse())

        endpoint.set(page)
        server.assertRequest(HttpMethod.PUT).withJsonBody(pageJson)
    }

    @Test
    fun testElementSuperConstructor() {
        class PageEndpoint(referrer: Endpoint) : ElementEndpointImpl<Page<MockEntity>>(referrer, "endpoint", javaTypeOf<Page<MockEntity>>())

        server.enqueue(MockResponse().setJsonBody(pageJson))
        assertEquals(page, PageEndpoint(entryEndpoint).read())
    }

    @Test
    fun testCollectionReadAll() {
        val endpoint = CollectionEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody("[$pageJson]"))

        assertEquals(listOf(page), endpoint.readAll())
    }

    @Test
    fun testCollectionGetByEntity() {
        val endpoint = CollectionEndpointImpl<MockEntity>(entryEndpoint, "endpoint")

        assertEquals("/endpoint/5", endpoint[MockEntity(5, "test")].uri.rawPath)
    }

    @Test
    fun testCollectionCreateAll() {
        val endpoint = CollectionEndpointImpl<Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.Accepted))

        endpoint.createAll(listOf(page))
        server.assertRequest(HttpMethod.PATCH).withJsonBody("[$pageJson]")
    }

    @Test
    fun testFunction() {
        val endpoint = FunctionEndpointImpl<List<MockEntity>, Page<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody(pageJson))

        assertEquals(page, endpoint.invoke(listOf(MockEntity(5, "test"))))
        server.assertRequest(HttpMethod.POST).withJsonBody("""[{"id":5,"name":"test"}]""")
    }

    @Test
    fun testProducer() {
        val endpoint = ProducerEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse().setJsonBody("""[{"id":5,"name":"test"}]"""))

        assertEquals(listOf(MockEntity(5, "test")), endpoint.invoke())
    }

    @Test
    fun testConsumer() {
        val endpoint = ConsumerEndpointImpl<List<MockEntity>>(entryEndpoint, "endpoint")
        server.enqueue(MockResponse())

        endpoint.invoke(listOf(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.POST).withJsonBody("""[{"id":5,"name":"test"}]""")
    }
}
