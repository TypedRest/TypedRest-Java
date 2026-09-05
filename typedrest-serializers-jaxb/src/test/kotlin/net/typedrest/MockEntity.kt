package net.typedrest

import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class MockEntity @JvmOverloads constructor(var id: Long = 0, var name: String = "")
