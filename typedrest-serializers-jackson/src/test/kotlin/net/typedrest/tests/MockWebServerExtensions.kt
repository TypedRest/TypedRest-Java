package net.typedrest.tests

import net.typedrest.http.HttpMethod
import net.typedrest.http.HttpStatusCode
import okhttp3.Headers
import okhttp3.mockwebserver.*
import java.nio.charset.Charset
import kotlin.test.*

const val contentTypeHeader = "Content-Type"

fun MockResponse.setResponseCode(code: HttpStatusCode): MockResponse {
    setResponseCode(code.code)
    return this
}

fun MockResponse.setJsonBody(json: String): MockResponse {
    addHeader(contentTypeHeader, "application/json")
    setBody(json)
    return this
}

fun MockWebServer.assertRequest(method: HttpMethod, path: String = "/endpoint"): RecordedRequest {
    val request = takeRequest()
    assertEquals(method.toString(), request.method)
    assertEquals(path, request.requestUrl!!.encodedPath)
    return request
}

fun RecordedRequest.withBody(expectedBody: ByteArray, contentType: String): RecordedRequest {
    assertEquals(contentType, getHeader(contentTypeHeader))
    assertContentEquals(expectedBody, body.readByteArray())
    return this
}

fun RecordedRequest.withJsonBody(json: String): RecordedRequest {
    assertEquals("application/json; charset=utf-8", getHeader(contentTypeHeader))
    assertEquals(json, body.readString(Charset.defaultCharset()))
    return this
}

fun RecordedRequest.withHeader(name: String, value: String): RecordedRequest {
    assertEquals(value, getHeader(name))
    return this
}
