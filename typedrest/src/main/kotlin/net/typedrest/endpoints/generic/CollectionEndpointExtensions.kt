package net.typedrest.endpoints.generic

import net.typedrest.errors.*
import net.typedrest.http.HttpStatusCode

/**
 * Determines whether the collection contains a specific element.
 *
 * @param id The ID identifying the element.
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws HttpException for other non-success status codes.
 */
fun <TElementEndpoint : ElementEndpoint<*>> IndexerEndpoint<TElementEndpoint>.contains(id: String): Boolean =
    this[id].exists()

/**
 * Determines whether the collection contains a specific element.
 *
 * @param entity An existing entity to extract the ID from.
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws HttpException for other non-success status codes.
 */
fun <TEntity, TElementEndpoint : ElementEndpoint<TEntity>> GenericCollectionEndpoint<TEntity, TElementEndpoint>.contains(entity: TEntity): Boolean =
    this[entity].exists()

/**
 * Sets/replaces an existing element in the collection.
 *
 * @param entity The entity to store. Its ID is used to identify the element to replace.
 * @return The entity as returned by the server, possibly with additional fields set. null if the server does not respond with a result entity.
 * @throws ConflictException when the entity has changed since it was last retrieved. Your changes were rejected to prevent a lost update.
 * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
 * @throws HttpException for other non-success status codes.
 */
fun <TEntity, TElementEndpoint : ElementEndpoint<TEntity>> GenericCollectionEndpoint<TEntity, TElementEndpoint>.set(entity: TEntity): TEntity? =
    this[entity].set(entity)

/**
 * Modifies an existing element in the collection by merging changes on the server-side.
 *
 * @param entity The entity data to merge with the existing one. Its ID is used to identify the element to modify.
 * @return The modified entity as returned by the server, possibly with additional fields set. null if the server does not respond with a result entity.
 * @throws ConflictException when the entity has changed since it was last retrieved. Your changes were rejected to prevent a lost update.
 * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
 * @throws HttpException for other non-success status codes.
 */
fun <TEntity, TElementEndpoint : ElementEndpoint<TEntity>> GenericCollectionEndpoint<TEntity, TElementEndpoint>.merge(entity: TEntity): TEntity? =
    this[entity].merge(entity)

/**
 * Deletes an existing element from the collection.
 *
 * @param id The ID identifying the element to delete.
 * @throws ConflictException when the entity has changed since it was last retrieved. Your delete call was rejected to prevent a lost update.
 * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
 * @throws HttpException for other non-success status codes.
 */
fun <TElementEndpoint : ElementEndpoint<*>> IndexerEndpoint<TElementEndpoint>.delete(id: String) =
    this[id].delete()

/**
 * Deletes an existing element from the collection.
 *
 * @param entity An existing entity to extract the ID from.
 * @throws ConflictException when the entity has changed since it was last retrieved. Your delete call was rejected to prevent a lost update.
 * @throws BadRequestException when the server responds with [HttpStatusCode.BadRequest].
 * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
 * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
 * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
 * @throws HttpException for other non-success status codes.
 */
fun <TEntity, TElementEndpoint : ElementEndpoint<TEntity>> GenericCollectionEndpoint<TEntity, TElementEndpoint>.delete(entity: TEntity) =
    this[entity].delete()
