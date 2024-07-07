package net.typedrest.serializers

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as JSON using Kotlinx.Serialization.
 */
open class KotlinxJsonSerializer : AbstractJsonSerializer() {
    override fun <T> serialize(entity: T, type: Class<T>): RequestBody =
        Json.encodeToString(getSerializer(type), entity).toRequestBody(mediaTypeJson)

    override fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody =
        Json.encodeToString(getListSerializer(type), entities.toList()).toRequestBody(mediaTypeJson)

    override fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        Json.decodeFromString(getSerializer(type), body.string())

    override fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        Json.decodeFromString(getListSerializer(type), body.string())

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSerializer(type: Type) =
        Json.serializersModule.serializer(type) as KSerializer<T>

    private fun <T> getListSerializer(type: Type) =
        getSerializer<List<T>>(object : ParameterizedType {
            override fun getOwnerType(): Type? = null
            override fun getRawType(): Type = List::class.java
            override fun getActualTypeArguments(): Array<Type> = arrayOf(type)
        })
}
