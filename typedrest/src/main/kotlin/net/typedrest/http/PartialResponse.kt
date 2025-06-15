package net.typedrest.http

/**
 * Represents a subset of a set of elements.
 *
 * @param elements The returned elements.
 * @param range The range the [elements] come from.
 * @param TEntity The type of element the response contains.
 */
class PartialResponse<TEntity>(val elements: List<TEntity>, val range: HttpContentRangeHeader?) {
    /**
     * Indicates whether the response reaches the end of the elements available on the server.
     */
    val endReached: Boolean
        get() = when {
            range?.to == null -> true // No range specified, must be complete response
            range.length == null -> false // No length specified, can't be the end
            else -> range.to == range.length - 1
        }
}
