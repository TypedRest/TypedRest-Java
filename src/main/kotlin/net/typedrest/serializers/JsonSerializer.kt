package net.typedrest.http

/**
 * Serializes and deserializes entities as JSON.
 */
class JsonSerializer : Serializer {
    val supportedMediaTypes = ["application/json"]

    serialize<T>(entity: T) {
        return JSON.stringify(entity)
    }

    deserialize<T>(text: string) {
        return JSON.parse(text) as T
    }
}
