package net.typedrest.endpoints.generic

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import net.typedrest.errors.ConcurrencyException
import net.typedrest.errors.ConflictException
import net.typedrest.http.*
import net.typedrest.tests.*
import net.typedrest.endpoints.EntryEndpoint
import okhttp3.*
import okhttp3.mockwebserver.*
import java.util.concurrent.*
import kotlin.test.*

class ElementEndpointTest : AbstractEndpointTest() {
    private val endpoint = ElementEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testRead() {
        server.enqueue(
            MockResponse()
                .setJsonBody("""{"id":5,"name":"test"}""")
        )

        assertEquals(MockEntity(5, "test"), endpoint.read())
    }

    @Test
    fun testReadCustomMimeWithJsonSuffix() {
        server.enqueue(
            MockResponse()
                .setJsonBody("""{"id":5,"name":"test"}""")
        )

        assertEquals(MockEntity(5, "test"), endpoint.read())
    }

    @Test
    fun testReadCacheETag() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setJsonBody("""{"id":5,"name":"test"}""")
        )
        val result1 = endpoint.read()
        server.assertRequest(HttpMethod.GET)
        assertEquals(MockEntity(5, "test"), result1)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        val result2 = endpoint.read()
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"123abc\"")
        assertEquals(MockEntity(5, "test"), result2)
        assertNotSame(result1, result2, message = "Cache responses, not deserialized objects")
    }

    @Test
    fun testReadCacheLastModified() {
        server.enqueue(
            MockResponse()
                .setHeader("Last-Modified", "Wed, 21 Oct 2015 00:00:00 GMT")
                .setJsonBody("""{"id":5,"name":"test"}""")
        )
        val result1 = endpoint.read()
        server.assertRequest(HttpMethod.GET)
        assertEquals(MockEntity(5, "test"), result1)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        val result2 = endpoint.read()
        server.assertRequest(HttpMethod.GET).withHeader("If-Modified-Since", "Wed, 21 Oct 2015 00:00:00 GMT")
        assertEquals(MockEntity(5, "test"), result2)
        assertNotSame(result1, result2, message = "Cache responses, not deserialized objects")
    }

    @Test
    fun testReadCacheNoCache() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setHeader("Cache-Control", "no-cache")
                .setJsonBody("""{"id":5,"name":"test"}""")
        )
        endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        assertEquals(MockEntity(5, "test"), endpoint.read())
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"123abc\"")
    }

    @Test
    fun testReadCacheExpired() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setHeader("Expires", "Wed, 21 Oct 2015 00:00:00 GMT")
                .setJsonBody("""{"id":5,"name":"test"}""")
        )
        endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        assertEquals(MockEntity(5, "test"), endpoint.read())
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"123abc\"")
    }

    @Test
    fun testExistsTrue() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.OK))
        assertTrue(endpoint.exists())
    }

    @Test
    fun testExistsFalse() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotFound))
        assertFalse(endpoint.exists())
    }

    @Test
    fun testSetResult() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"testXXX"}"""))

        assertEquals(MockEntity(5, "testXXX"), endpoint.set(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PUT)
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testSetNoResult() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        assertNull(endpoint.set(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PUT)
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testSetETag() {
        server.enqueue(
            MockResponse()
                .setHeader("ETag", "\"123abc\"")
                .setJsonBody("""{"id":5,"name":"test"}""")
        )
        val result = endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(result)
        server.assertRequest(HttpMethod.PUT)
            .withHeader("If-Match", "\"123abc\"")
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    /**
     * Holds back the next GET request on the client side until [release] is called, so other requests can overtake it.
     */
    private class DelayedReadInterceptor : Interceptor {
        private val received = CountDownLatch(1)
        private val released = CountDownLatch(1)

        @Volatile
        var armed = false

        override fun intercept(chain: Interceptor.Chain): Response {
            if (armed && chain.request().method == "GET") {
                armed = false
                received.countDown()
                assertTrue(released.await(10, TimeUnit.SECONDS), "Delayed request was not released")
            }
            return chain.proceed(chain.request())
        }

        fun awaitReceived() = assertTrue(received.await(10, TimeUnit.SECONDS), "Delayed request was not received")
        fun release() = released.countDown()
    }

    private class DelayedRead(
        val endpoint: ElementEndpointImpl<MockEntity>,
        private val interceptor: DelayedReadInterceptor,
        private val server: MockWebServer,
        private val delayedResponse: MockResponse,
        private val read: Future<MockEntity>
    ) {
        /**
         * Lets the delayed read continue and answers it with the delayed response.
         *
         * @return The result of the read.
         */
        fun release(): MockEntity {
            server.enqueue(delayedResponse)
            interceptor.release()
            return read.get(10, TimeUnit.SECONDS)
        }

        val cachedETag get() = endpoint.responseCache?.ifModifiedHeaders()?.get("If-None-Match")
    }

    /**
     * Reads the entity with ETag `"v1"`, then starts another read that is held back until [DelayedRead.release] is called.
     */
    private fun startDelayedRead(response: MockResponse): DelayedRead {
        val interceptor = DelayedReadInterceptor()
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val endpoint = ElementEndpointImpl(EntryEndpoint(server.url("/").toUri(), client), "endpoint", MockEntity::class.java)

        server.enqueue(MockResponse().setHeader("ETag", "\"v1\"").setJsonBody("""{"id":5,"name":"test"}"""))
        endpoint.read()

        interceptor.armed = true
        val read = CompletableFuture.supplyAsync { endpoint.read() }
        interceptor.awaitReceived()
        return DelayedRead(endpoint, interceptor, server, response, read)
    }

    @Test
    fun testReadNotModifiedDuringSet() {
        val delayed = startDelayedRead(MockResponse().setResponseCode(HttpStatusCode.NotModified))

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent).setHeader("ETag", "\"v2\""))
        delayed.endpoint.set(MockEntity(5, "changed"))

        // The server confirmed the version the read was based on, even though the cache has moved on since
        assertEquals(MockEntity(5, "test"), delayed.release())
        assertEquals("\"v2\"", delayed.cachedETag)
    }

    @Test
    fun testSetWeakETag() {
        server.enqueue(MockResponse().setHeader("ETag", "W/\"v1\"").setJsonBody("""{"id":5,"name":"test"}"""))
        endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(MockEntity(5, "changed"))
        // If-Match uses strong comparison, so a weak ETag would never match
        assertNull(server.assertRequest(HttpMethod.PUT).getHeader("If-Match"))
    }

    @Test
    fun testReadWeakETag() {
        server.enqueue(MockResponse().setHeader("ETag", "W/\"v1\"").setJsonBody("""{"id":5,"name":"test"}"""))
        endpoint.read()
        server.assertRequest(HttpMethod.GET)

        // If-None-Match uses weak comparison, so weak ETags are still useful there
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        assertEquals(MockEntity(5, "test"), endpoint.read())
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "W/\"v1\"")
    }

    @Test
    fun testSetCacheETag() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent).setHeader("ETag", "\"456def\""))
        endpoint.set(MockEntity(5, "test"))
        server.assertRequest(HttpMethod.PUT).withJsonBody("""{"id":5,"name":"test"}""")

        assertEquals(MockEntity(5, "test"), endpoint.response)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NotModified))
        val result = endpoint.read()
        server.assertRequest(HttpMethod.GET).withHeader("If-None-Match", "\"456def\"")
        assertEquals(MockEntity(5, "test"), result)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(result)
        server.assertRequest(HttpMethod.PUT)
            .withHeader("If-Match", "\"456def\"")
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testSetCacheETagWithResult() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"testXXX"}""").setHeader("ETag", "\"456def\""))
        val result = endpoint.set(MockEntity(5, "test"))
        server.assertRequest(HttpMethod.PUT).withJsonBody("""{"id":5,"name":"test"}""")
        assertEquals(MockEntity(5, "testXXX"), result)

        assertEquals(MockEntity(5, "testXXX"), endpoint.response)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.delete()
        server.assertRequest(HttpMethod.DELETE).withHeader("If-Match", "\"456def\"")
    }

    @Test
    fun testSetNoETag() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(MockEntity(5, "test"))
        server.assertRequest(HttpMethod.PUT)

        assertNull(endpoint.response)
    }

    @Test
    fun testSetLastModified() {
        server.enqueue(
            MockResponse()
                .setJsonBody("""{"id":5,"name":"test"}""")
                .addHeader("Last-Modified", "Wed, 21 Oct 2015 00:00:00 GMT")
        )
        val result = endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.set(result)
        server.assertRequest(HttpMethod.PUT)
            .withHeader("If-Unmodified-Since", "Wed, 21 Oct 2015 00:00:00 GMT")
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testSetPreconditionFailed() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.PreconditionFailed))

        val ex = assertFailsWith<ConcurrencyException> { endpoint.set(MockEntity(5, "test")) }
        assertIs<ConflictException>(ex, "Must stay catchable as ConflictException")
        assertEquals(HttpStatusCode.PreconditionFailed, ex.status)
    }

    @Test
    fun testSetConflict() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.Conflict))

        val ex = assertFailsWith<ConflictException> { endpoint.set(MockEntity(5, "test")) }
        assertEquals(ConflictException::class, ex::class)
    }

    @Test
    fun testUpdateRetry() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"test1"}""").addHeader("ETag", "\"1\""))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.PreconditionFailed))
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"test2"}""").addHeader("ETag", "\"2\""))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        endpoint.update({ MockEntity(it.id, "testX") })

        server.assertRequest(HttpMethod.GET)
        server.assertRequest(HttpMethod.PUT).withJsonBody("""{"id":5,"name":"testX"}""").withHeader("If-Match", "\"1\"")
        server.assertRequest(HttpMethod.GET)
        server.assertRequest(HttpMethod.PUT).withJsonBody("""{"id":5,"name":"testX"}""").withHeader("If-Match", "\"2\"")
    }

    @Test
    fun testUpdateFail() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"test1"}""").addHeader("ETag", "\"1\""))
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.PreconditionFailed))

        assertFailsWith<ConflictException> {
            endpoint.update({ MockEntity(it.id, "testX") }, maxRetries = 0)
        }

        server.assertRequest(HttpMethod.GET)
        server.assertRequest(HttpMethod.PUT).withJsonBody("""{"id":5,"name":"testX"}""").withHeader("If-Match", "\"1\"")
    }

    @Test
    fun testMergeResult() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"testXXX"}"""))

        assertEquals(MockEntity(5, "testXXX"), endpoint.merge(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PATCH)
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testMergeNoResult() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        assertNull(endpoint.merge(MockEntity(5, "test")))
        server.assertRequest(HttpMethod.PATCH)
            .withJsonBody("""{"id":5,"name":"test"}""")
    }

    @Test
    fun testDelete() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))

        endpoint.delete()
        server.assertRequest(HttpMethod.DELETE)
    }

    @Test
    fun testDeleteETag() {
        server.enqueue(MockResponse().setJsonBody("""{"id":5,"name":"test"}""").addHeader("ETag", "\"123abc\""))
        endpoint.read()
        server.assertRequest(HttpMethod.GET)

        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.NoContent))
        endpoint.delete()
        server.assertRequest(HttpMethod.DELETE)
            .withHeader("If-Match", "\"123abc\"")
    }
}
