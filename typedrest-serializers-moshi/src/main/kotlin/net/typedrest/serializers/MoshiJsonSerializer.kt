package net.typedrest.serializers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 * Serializes and deserializes entities as JSON using Moshi.
 *
 * @param moshi The Moshi instance to use for serializing and deserializing. Uses default instance with reflection-based Kotlin support if unset.
 */
open class MoshiJsonSerializer @JvmOverloads constructor(
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
) : AbstractJsonSerializer() {
    override fun <T> serialize(entity: T, type: Type): RequestBody =
        moshi.adapter<T>(type).toJson(entity).toRequestBody(mediaTypeJson)

    override fun <T> deserialize(body: ResponseBody, type: Type): T? =
        moshi.adapter<T>(type).fromJson(body.source())
}
