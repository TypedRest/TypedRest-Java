package net.typedrest.endpoints

import okhttp3.mockwebserver.*
import kotlin.test.*

abstract class AbstractEndpointTest {
    protected var server: MockWebServer = MockWebServer()
    protected var entryEndpoint: EntryEndpoint = EntryEndpoint(server.url("/").toUri())

    @AfterTest
    fun after() = server.close()
}
