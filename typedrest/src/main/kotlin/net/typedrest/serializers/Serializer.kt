package net.typedrest.serializers

import okhttp3.*

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
     * @param type The type of entity to deserialize.
     * @param T The type of entity to serialize.
     * @return The serialized entity as a request body.
     */
    fun <T> serialize(entity: T, type: Class<T>): RequestBody

    /**
     * Serializes a list of entities.
     *
     * @param entities The entities to serialize.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to serialize.
     * @return The serialized entities as a request body.
     */
    fun <T> serializeList(entities: Iterable<T>, type: Class<T>): RequestBody

    /**
     * Deserializes an entity.
     *
     * @param body The request body to deserialize into an entity.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to deserialize.
     * @return The deserialized response body as an entity. null if the body could not be deserialized.
     */
    fun <T> deserialize(body: ResponseBody, type: Class<T>): T?

    /**
     * Deserializes a list of entities.
     *
     * @param body The request body to deserialize into an entity.
     * @param type The type of entity to deserialize.
     * @param T The type of entity to deserialize.
     * @return The deserialized response body as a list of entities. null if the body could not be deserialized.
     */
    fun <T> deserializeList(body: ResponseBody, type: Class<T>): List<T>?
}
