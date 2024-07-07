package net.typedrest.http

enum class HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS;

    companion object {
        @JvmStatic
        fun parse(value: String) = when (value.uppercase()) {
            "GET" -> GET
            "POST" -> POST
            "PUT" -> PUT
            "PATCH" -> PATCH
            "DELETE" -> DELETE
            "HEAD" -> HEAD
            "OPTIONS" -> OPTIONS
            else -> null
        }
    }
}
