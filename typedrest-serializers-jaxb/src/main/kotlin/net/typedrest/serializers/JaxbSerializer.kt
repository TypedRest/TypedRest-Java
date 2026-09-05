package net.typedrest.serializers

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAnyElement
import jakarta.xml.bind.annotation.XmlRootElement
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

/**
 * Serializes and deserializes entities as XML using JAXB.
 *
 * Entity classes must be annotated for JAXB binding (e.g. with `@XmlRootElement`) and provide a no-arg constructor.
 */
open class JaxbSerializer : AbstractXmlSerializer() {
    // JAXBContext instances are expensive to create, so one is cached per entity type.
    private val contexts = ConcurrentHashMap<Class<*>, JAXBContext>()

    private fun contextFor(type: Class<*>): JAXBContext =
        contexts.computeIfAbsent(type) { JAXBContext.newInstance(it, ListWrapper::class.java) }

    override fun <T> serialize(entity: T, type: Class<T>): RequestBody {
        val writer = StringWriter()
        contextFor(type).createMarshaller().marshal(entity, writer)
        return writer.toString().toRequestBody(mediaTypeXml)
    }

    override fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody {
        val writer = StringWriter()
        contextFor(type).createMarshaller().marshal(ListWrapper(ArrayList<Any?>(entities.toList())), writer)
        return writer.toString().toRequestBody(mediaTypeXml)
    }

    override fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        body.byteStream().use {
            @Suppress("UNCHECKED_CAST")
            contextFor(type).createUnmarshaller().unmarshal(it) as T?
        }

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        body.byteStream().use {
            val wrapper = contextFor(type).createUnmarshaller().unmarshal(it) as ListWrapper
            wrapper.items as List<T>
        }
}

/**
 * Wraps a list of arbitrary JAXB-bound entities so JAXB can (de)serialize it as a single XML document, since it
 * cannot bind a bare list on its own. Each item is (de)serialized using its own `@XmlRootElement` binding.
 */
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
private class ListWrapper() {
    @XmlAnyElement(lax = true)
    var items: MutableList<Any?> = mutableListOf()

    constructor(items: MutableList<Any?>) : this() {
        this.items = items
    }
}
