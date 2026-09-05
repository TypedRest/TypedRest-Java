package net.typedrest.tests

import net.typedrest.MockEntity
import net.typedrest.http.HttpMethod
import net.typedrest.http.HttpStatusCode
import net.typedrest.serializers.JacksonXmlSerializer
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.*
import okio.Buffer
import java.nio.charset.Charset
import kotlin.test.*

const val contentTypeHeader = "Content-Type"

private val serializer = JacksonXmlSerializer()

fun MockResponse.setResponseCode(code: HttpStatusCode): MockResponse {
    setResponseCode(code.code)
    return this
}

fun MockResponse.setXmlBody(entity: MockEntity): MockResponse {
    addHeader(contentTypeHeader, "application/xml")
    val buffer = Buffer()
    serializer.serialize(entity, MockEntity::class.java).writeTo(buffer)
    setBody(buffer.readString(Charsets.UTF_8))
    return this
}

fun MockResponse.setXmlListBody(entities: List<MockEntity>): MockResponse {
    addHeader(contentTypeHeader, "application/xml")
    val buffer = Buffer()
    serializer.serializeList(entities, MockEntity::class.java).writeTo(buffer)
    setBody(buffer.readString(Charsets.UTF_8))
    return this
}

fun MockWebServer.assertRequest(method: HttpMethod, path: String = "/endpoint"): RecordedRequest {
    val request = takeRequest()
    assertEquals(method.toString(), request.method)
    assertEquals(path, request.requestUrl!!.encodedPath)
    return request
}

fun RecordedRequest.withHeader(name: String, value: String): RecordedRequest {
    assertEquals(value, getHeader(name))
    return this
}

fun RecordedRequest.withXmlBody(entity: MockEntity): RecordedRequest {
    assertEquals("application/xml; charset=utf-8", getHeader(contentTypeHeader))
    val body = body.readString(Charset.defaultCharset()).toResponseBody(null)
    assertEquals(entity, serializer.deserialize(body, MockEntity::class.java))
    return this
}

fun RecordedRequest.withXmlListBody(entities: List<MockEntity>): RecordedRequest {
    assertEquals("application/xml; charset=utf-8", getHeader(contentTypeHeader))
    val body = body.readString(Charset.defaultCharset()).toResponseBody(null)
    assertEquals(entities, serializer.deserializeList(body, MockEntity::class.java))
    return this
}
