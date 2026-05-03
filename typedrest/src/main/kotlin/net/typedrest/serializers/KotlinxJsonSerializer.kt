package net.typedrest.serializers

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as JSON using Kotlinx.Serialization.
 *
 * @param json The Kotlinx [Json] instance to use. Defaults to omitting `null` properties when writing and tolerating unknown properties when reading.
 */
open class KotlinxJsonSerializer @JvmOverloads constructor(
    private val json: Json = defaultJson
) : AbstractJsonSerializer() {
    companion object {
        @JvmStatic
        val defaultJson: Json = Json {
            explicitNulls = false
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override fun <T> serialize(entity: T, type: Class<T>): RequestBody =
        json.encodeToString(getSerializer(type), entity).toRequestBody(mediaTypeJson)

    override fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody =
        json.encodeToString(getListSerializer(type), entities.toList()).toRequestBody(mediaTypeJson)

    override fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        json.decodeFromString(getSerializer(type), body.string())

    override fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        json.decodeFromString(getListSerializer(type), body.string())

    @Suppress("UNCHECKED_CAST")
    private fun <T> getSerializer(type: Type) =
        json.serializersModule.serializer(type) as KSerializer<T>

    private fun <T> getListSerializer(type: Type) =
        getSerializer<List<T>>(object : ParameterizedType {
            override fun getOwnerType(): Type? = null
            override fun getRawType(): Type = List::class.java
            override fun getActualTypeArguments(): Array<Type> = arrayOf(type)
        })
}
