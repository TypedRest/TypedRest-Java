package net.typedrest.endpoints.generic

import net.typedrest.endpoints.Endpoint
import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * Endpoint for an individual resource.
 *
 * @param TEntity The type of entity the endpoint represents.
 */
interface ElementEndpoint<TEntity> : Endpoint {
    /**
     * Determines whether the element currently exists.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws HttpException for other non-success status codes.
     */
    fun exists(): Boolean

    /**
     * A cached copy of the entity as received from the server.
     */
    val response: TEntity?

    /**
     * Returns the entity.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun read(): TEntity

    /**
     * Shows whether the server has indicated that [set] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isSetAllowed: Boolean?

    /**
     * Sets/replaces the entity.
     *
     * @param entity The new entity.
     * @return The entity as returned by the server, possibly with additional fields set. null if the server does not respond with a result entity.
     * @throws ConflictException when the entity has changed since it was last retrieved with [read]. Your changes were rejected to prevent a lost update.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun set(entity: TEntity): TEntity?

    /**
     * Shows whether the server has indicated that [merge] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isMergeAllowed: Boolean?

    /**
     * Modifies an existing entity by merging changes on the server-side.
     *
     * @param entity The entity data to merge with the existing one.
     * @return The modified entity as returned by the server, possibly with additional fields set. null if the server does not respond with a result entity.
     * @throws ConflictException when the entity has changed since it was last retrieved with [read]. Your changes were rejected to prevent a lost update.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun merge(entity: TEntity): TEntity?

    /**
     * Reads the current state of the entity, applies a change to it and stores the result. Applies optimistic concurrency using automatic retries.
     *
     * @param updateAction A callback that takes the current state of the entity and returns it with the desired modifications applied.
     * @param maxRetries The maximum number of retries to perform for optimistic concurrency before giving up.
     * @return The entity as returned by the server, possibly with additional fields set. null if the server does not respond with a result entity.
     * @throws ConflictException The number of retries performed for optimistic concurrency exceeded [maxRetries].
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun update(updateAction: (TEntity) -> TEntity, maxRetries: Int = 3): TEntity?

    /**
     * Shows whether the server has indicated that [delete] is currently allowed.
     *
     * Uses cached data from last response.
     *
     * @return true if the method is allowed, false if the method is not allowed, null if no request has been sent yet or the server did not specify allowed methods.
     */
    val isDeleteAllowed: Boolean?

    /**
     * Deletes the element.
     *
     * @throws ConflictException when the entity has changed since it was last retrieved with [read]. Your delete call was rejected to prevent a lost update.
     * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun delete()
}
