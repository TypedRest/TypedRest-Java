package net.typedrest

import java.lang.reflect.*
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

/**
 * Returns the Java [Type] for [T], including generic type arguments (e.g., `Page<Contact>`).
 *
 * Use this to pass entity types to endpoint constructors, e.g., in super-constructor calls where the reified factory functions are not available.
 */
@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T> javaTypeOf(): Type = typeOf<T>().javaType

/**
 * Returns the class or interface that a [Type] erases to, or null if it cannot be determined (e.g., for type variables).
 */
internal fun Type.rawClass(): Class<*>? = when (this) {
    is Class<*> -> this
    is ParameterizedType -> rawType.rawClass()
    is GenericArrayType -> genericComponentType.rawClass()?.let { java.lang.reflect.Array.newInstance(it, 0).javaClass }
    is WildcardType -> upperBounds.firstOrNull()?.rawClass()
    else -> null
}

/**
 * Returns a short, human-readable name for a [Type], for use in error messages.
 */
internal val Type.displayName: String
    get() = (this as? Class<*>)?.simpleName ?: typeName

/**
 * Builds the type `List<elementType>`.
 */
internal fun listTypeOf(elementType: Type): ParameterizedType = ListType(elementType)

private class ListType(private val elementType: Type) : ParameterizedType {
    override fun getRawType(): Type = List::class.java
    override fun getOwnerType(): Type? = null
    override fun getActualTypeArguments(): Array<Type> = arrayOf(elementType)

    override fun equals(other: Any?) =
        other is ParameterizedType && other.rawType == rawType && other.ownerType == null && other.actualTypeArguments.contentEquals(actualTypeArguments)

    override fun hashCode() = actualTypeArguments.contentHashCode() xor rawType.hashCode()

    override fun toString() = "${List::class.java.name}<${elementType.typeName}>"
}
