package net.typedrest.serializers

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Serializes and deserializes entities as JSON using Moshi.
 *
 * @param moshi The Moshi instance to use for serializing and deserializing. Uses default instance with reflection-based Kotlin support if unset.
 */
open class MoshiJsonSerializer @JvmOverloads constructor(
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
) : AbstractJsonSerializer() {
    override fun <T> serialize(entity: T, type: Class<T>): RequestBody =
        moshi.adapter(type).toJson(entity).toRequestBody(mediaTypeJson)

    override fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody =
        moshi.adapter<Iterable<T>>(Types.newParameterizedType(List::class.java, type)).toJson(entities.toList()).toRequestBody(mediaTypeJson)

    override fun <T> deserialize(body: ResponseBody, type: Class<T>): T? =
        moshi.adapter(type).fromJson(body.source())

    override fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>? =
        moshi.adapter<List<T>>(Types.newParameterizedType(List::class.java, type)).fromJson(body.source())
}
