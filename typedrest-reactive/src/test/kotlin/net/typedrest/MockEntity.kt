package net.typedrest

import kotlinx.serialization.Serializable

@Serializable
data class MockEntity(val id: Long, val name: String)
