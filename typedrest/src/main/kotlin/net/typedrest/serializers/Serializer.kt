package net.typedrest.serializers

import net.typedrest.listTypeOf
import okhttp3.*
import java.lang.reflect.Type

/**
 * Controls the serialization of entities sent to and received from the server.
 */
interface Serializer {
    /**
     * A list of MIME types this serializer supports.
     */
    val supportedMediaTypes: List<MediaType>

    /**
     * Serializes an entity.
     *
     * @param entity The entity to serialize.
     * @param type The type of entity to serialize.
     * @param T The type of entity to serialize.
     * @return The serialized entity as a request body.
     */
    fun <T> serialize(entity: T, type: Type): RequestBody

    /**
     * Serializes a list of entities.
     *
     * @param entities The entities to serialize.
     * @param elementType The type of the individual entities.
     * @param T The type of the individual entities.
     * @return The serialized entities as a request body.
     */
    fun <T> serializeList(entities: Iterable<T>, elementType: Type): RequestBody =
        serialize(entities.toList(), listTypeOf(elementType))

    /**
     * Deserializes an entity.
     *
     * @param body The response body to deserialize into an entity.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to deserialize.
     * @return The deserialized response body as an entity. null if the body could not be deserialized.
     */
    fun <T> deserialize(body: ResponseBody, type: Type): T?

    /**
     * Deserializes a list of entities.
     *
     * @param body The response body to deserialize into a list of entities.
     * @param elementType The type of the individual entities.
     * @param T The type of the individual entities.
     * @return The deserialized response body as a list of entities. null if the body could not be deserialized.
     */
    fun <T> deserializeList(body: ResponseBody, elementType: Type): List<T>? =
        deserialize(body, listTypeOf(elementType))
}
