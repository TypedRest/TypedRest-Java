package net.typedrest.endpoints.generic

import net.typedrest.MockEntity
import net.typedrest.endpoints.AbstractEndpointTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexerEndpointTest : AbstractEndpointTest() {
    private val endpoint = IndexerEndpointImpl<ElementEndpoint<MockEntity>>(entryEndpoint, "endpoint") { ref, uri -> ElementEndpointImpl(ref, uri, MockEntity::class.java) }

    @Test
    fun testGetById() {
        assertEquals("/endpoint/x%2Fy", endpoint["x/y"].uri.path)
    }
}
