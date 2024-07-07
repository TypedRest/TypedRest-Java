package net.typedrest.endpoints.generic

import net.typedrest.errors.ConflictException
import net.typedrest.http.*
import net.typedrest.errors.*

/**
 * Endpoint for a collection of [TEntity]s addressable as [TElementEndpoint]s.
 *
 * Use the more constrained [CollectionEndpoint] when possible.
 *
 * @param TEntity The type of individual elements in the collection.
 * @param TElementEndpoint The type of [ElementEndpoint] to provide for individual [TEntity]s.
 */
interface GenericCollectionEndpoint<TEntity, out TElementEndpoint : ElementEndpoint<TEntity>> {
    /**
     * Returns an [ElementEndpoint] for a specific child element.
     *
     * @param entity An existing entity to extract the ID from.
     * @return The [TElementEndpoint] for the specified entity.
     */
    operator fun get(entity: TEntity): TElementEndpoint

    /**
     * Shows whether the server has indicated that [readAll] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null If no request has been sent yet or the server did not specify allowed methods.
     */
    val readAllAllowed: Boolean?

    /**
     * Returns all entities in the collection.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     * @return The list of all [TEntity].
     */
    fun readAll(): List<TEntity>

    /**
     * Shows whether the server has indicated that [readRange] is allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the method is allowed. If no request has been sent yet.
     */
    val readRangeAllowed: Boolean?

    /**
     * Returns all entities within a specific range of the collection.
     *
     * @param from The position at which to start sending data.
     * @param to The position at which to stop sending data.
     * @return A [PartialResponse] containing a subset of the entities and the range they come from.
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws ConflictException if the requested range is not satisfiable.
     * @throws HttpException for other non-success status codes.
     */
    fun readRange(from: Long?, to: Long?): PartialResponse<TEntity>

    /**
     * Shows whether the server has indicated that [create] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null If no request has been sent yet or the server did not specify allowed methods.
     */
    val createAllowed: Boolean?

    /**
     * Adds an entity as a new element to the collection.
     *
     * @param entity The new entity.
     * @return An endpoint for the newly created entity; `null` if the server returned neither a "Location" header nor an entity with an ID in the response body.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws ConflictException for when the server responds with [HttpStatusCode.Conflict].
     * @throws HttpException for other non-success status codes.
     */
    fun create(entity: TEntity): TElementEndpoint?

    /**
     * Shows whether the server has indicated that [createAll] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been sent yet or the server did not specify allowed verbs `null` is returned.
     */
    val createAllAllowed: Boolean?

    /**
     * Adds (or updates) multiple entities as elements in the collection.
     *
     * Uses a link with the relation type `bulk` to determine the URI to POST to. Defaults to the relative URI `bulk`.
     *
     * @param entities The entities to create or modify.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws ConflictException for when the server responds with [HttpStatusCode.Conflict].
     * @throws HttpException for other non-success status codes.
     */
    fun createAll(entities: Iterable<TEntity>)

    /**
     * Shows whether the server has indicated that [setAll] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return An indicator whether the verb is allowed. If no request has been sent yet or the server did not specify allowed verbs `null` is returned.
     */
    val setAllAllowed: Boolean?

    /**
     * Replaces the entire content of the collection with new entities.
     *
     * @param entities The new set of entities the collection shall contain.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws ConflictException when the entity has changed since it was last retrieved with [readAll]. Your changes were rejected to prevent a lost update.
     * @throws HttpException for other non-success status codes.
     */
    fun setAll(entities: Iterable<TEntity>)
}
