package net.typedrest.serializers

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAnyElement
import jakarta.xml.bind.annotation.XmlRootElement
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.StringWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.concurrent.ConcurrentHashMap

/**
 * Serializes and deserializes entities as XML using JAXB.
 *
 * Entity classes must be annotated for JAXB binding (e.g. with `@XmlRootElement`) and provide a no-arg constructor.
 * JAXB cannot bind generic types, so the only supported generic type is a `List` (or other `Iterable`) of such entities.
 */
open class JaxbSerializer : AbstractXmlSerializer() {
    // JAXBContext instances are expensive to create, so one is cached per entity type.
    private val contexts = ConcurrentHashMap<Class<*>, JAXBContext>()

    private fun contextFor(type: Class<*>): JAXBContext =
        contexts.computeIfAbsent(type) { JAXBContext.newInstance(it, ListWrapper::class.java) }

    override fun <T> serialize(entity: T, type: Type): RequestBody {
        val writer = StringWriter()
        val elementType = type.listElementType()
        if (elementType == null) contextFor(type.toClass()).createMarshaller().marshal(entity, writer)
        else contextFor(elementType).createMarshaller().marshal(ListWrapper(ArrayList<Any?>((entity as Iterable<*>).toList())), writer)
        return writer.toString().toRequestBody(mediaTypeXml)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserialize(body: ResponseBody, type: Type): T? =
        body.byteStream().use {
            val elementType = type.listElementType()
            if (elementType == null) contextFor(type.toClass()).createUnmarshaller().unmarshal(it) as T?
            else (contextFor(elementType).createUnmarshaller().unmarshal(it) as ListWrapper).items as T?
        }

    /**
     * Returns the element class if this type is a `List` or other `Iterable`, otherwise null.
     */
    private fun Type.listElementType(): Class<*>? =
        (this as? ParameterizedType)
            ?.takeIf { Iterable::class.java.isAssignableFrom(it.rawType.toClass()) }
            ?.actualTypeArguments?.single()?.toClass()

    private fun Type.toClass(): Class<*> = when (this) {
        is Class<*> -> this
        is ParameterizedType -> rawType.toClass()
        is WildcardType -> upperBounds.first().toClass()
        else -> throw IllegalArgumentException("JAXB cannot bind type $typeName")
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
