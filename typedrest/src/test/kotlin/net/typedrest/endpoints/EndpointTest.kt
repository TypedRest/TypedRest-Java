package net.typedrest.endpoints

import net.typedrest.MockEntity
import net.typedrest.endpoints.generic.ElementEndpointImpl
import net.typedrest.errors.HttpException
import net.typedrest.errors.NotFoundException
import net.typedrest.http.*
import net.typedrest.tests.*
import okhttp3.mockwebserver.MockResponse
import kotlin.test.*

class EndpointTest : AbstractEndpointTest() {
    private val endpoint = ElementEndpointImpl(entryEndpoint, "endpoint", MockEntity::class.java)

    @Test
    fun testLinkLazyLookup() {
        server.enqueue(MockResponse().addHeader("Link", "<a>; rel=target"))

        assertEquals(server.url("/a").toUri(), endpoint.link("target"))
        server.assertRequest(HttpMethod.HEAD)
    }

    @Test
    fun testLinkLazyLookupHalContentType() {
        server.enqueue(
            MockResponse()
                .addHeader(contentTypeHeader, "application/hal+json")
                .addHeader("Link", "<a>; rel=target")
        )

        assertEquals(server.url("/a").toUri(), endpoint.link("target"))
        server.assertRequest(HttpMethod.HEAD)
    }

    @Test
    fun testLinkLazyLookupOnlyOnce() {
        server.enqueue(MockResponse().addHeader("Link", "<a>; rel=target"))

        endpoint.link("target")
        endpoint.link("target")
        assertEquals(1, server.requestCount)
    }

    @Test
    fun testLinkLazyLookupNotProvided() {
        server.enqueue(MockResponse().addHeader("Link", "<a>; rel=other"))

        assertFailsWith<NotFoundException> { endpoint.link("target") }
        server.assertRequest(HttpMethod.HEAD)
    }

    @Test
    fun testLinkLazyLookupError() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.InternalServerError))

        val ex = assertFailsWith<NotFoundException> { endpoint.link("target") }
        assertIs<HttpException>(ex.cause)
    }

    @Test
    fun testLinkTemplateLazyLookup() {
        server.enqueue(MockResponse().addHeader("Link", "<a{?x}>; rel=target; templated=true"))

        assertEquals(server.url("/a?x=1").toUri(), endpoint.linkTemplate("target", mapOf("x" to "1")))
        server.assertRequest(HttpMethod.HEAD)
    }

    @Test
    fun testLinkTemplateLazyLookupNotProvided() {
        server.enqueue(MockResponse())

        assertFailsWith<NotFoundException> { endpoint.linkTemplate("target", mapOf("x" to "1")) }
        server.assertRequest(HttpMethod.HEAD)
    }

    @Test
    fun testLinkTemplateLazyLookupError() {
        server.enqueue(MockResponse().setResponseCode(HttpStatusCode.InternalServerError))

        val ex = assertFailsWith<NotFoundException> { endpoint.linkTemplate("target", mapOf("x" to "1")) }
        assertIs<HttpException>(ex.cause)
    }
}
