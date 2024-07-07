package net.typedrest.http

import okhttp3.Headers

/**
 * @param from The position at which the data starts.
 * @param to The position at which the data stops.
 * @param length The starting or ending point of the range.
 */
data class HttpContentRangeHeader(val unit: String, val from: Long?, val to: Long?, val length: Long?) {
    companion object {
        @JvmStatic
        fun parse(headers: Headers): HttpContentRangeHeader? {
            val header = headers["Content-Range"] ?: return null
            val matchResult = Regex("(\\w+) (\\d+)-(\\d+)/(\\d+|\\*)").find(header) ?: return null

            val (unit, from, to, length) = matchResult.destructured
            return HttpContentRangeHeader(
                unit = unit,
                from = from.toLongOrNull(),
                to = to.toLongOrNull(),
                length = if (length == "*") null else length.toLongOrNull()
            )
        }
    }
}
