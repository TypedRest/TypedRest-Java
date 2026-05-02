package net.typedrest.links

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlin.test.Test
import kotlin.test.assertEquals

class HalLinkExtractorTest {
    private val extractor = HalLinkExtractor()

    private fun response(body: String): Response =
        Response.Builder()
            .request(Request.Builder().url("http://localhost/").build())
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body.toResponseBody("application/hal+json".toMediaType()))
            .build()

    private fun List<Link>.tuples() =
        map { listOf(it.rel, it.href, it.title, it.templated) }

    @Test
    fun testSingleLink() {
        val body = """{"_links":{"single":{"href":"a"}}}"""
        assertEquals(
            listOf(listOf("single", "a", null, false)),
            extractor.getLinks(response(body)).tuples()
        )
    }

    @Test
    fun testArrayOfLinks() {
        val body = """{"_links":{"collection":[{"href":"b","title":"Title 1"},{"href":"c"}]}}"""
        assertEquals(
            listOf(
                listOf("collection", "b", "Title 1", false),
                listOf("collection", "c", null, false)
            ),
            extractor.getLinks(response(body)).tuples()
        )
    }

    @Test
    fun testTemplatedLink() {
        val body = """{"_links":{"template":{"href":"{id}","templated":true}}}"""
        assertEquals(
            listOf(listOf("template", "{id}", null, true)),
            extractor.getLinks(response(body)).tuples()
        )
    }

    @Test
    fun testMixed() {
        val body = """{"_links":{"single":{"href":"a"},"collection":[{"href":"b","title":"Title 1"},{"href":"c"}],"template":{"href":"{id}","templated":true}}}"""
        assertEquals(
            listOf(
                listOf("single", "a", null, false),
                listOf("collection", "b", "Title 1", false),
                listOf("collection", "c", null, false),
                listOf("template", "{id}", null, true)
            ),
            extractor.getLinks(response(body)).tuples()
        )
    }
}
