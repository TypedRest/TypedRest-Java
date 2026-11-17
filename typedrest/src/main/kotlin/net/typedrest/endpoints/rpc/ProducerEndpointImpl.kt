package net.typedrest.endpoints.rpc

import net.typedrest.displayName
import net.typedrest.endpoints.*
import net.typedrest.http.uri
import net.typedrest.javaTypeOf
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.net.URI

/**
 * RPC endpoint that takes no input and returns [TResult] as output when invoked.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param resultType The type of entity the endpoint returns as output.
 * @param TResult The type of entity the endpoint returns as output.
 */
open class ProducerEndpointImpl<TResult>(
    referrer: Endpoint,
    relativeUri: URI,
    private val resultType: Type
) : AbstractRpcEndpoint(referrer, relativeUri), ProducerEndpoint<TResult> {
    /**
     * Creates a new producer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param resultType The type of entity the endpoint returns as output.
     */
    constructor(referrer: Endpoint, relativeUri: String, resultType: Type) :
        this(referrer, URI(relativeUri), resultType)

    /**
     * Creates a new producer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
     * @param resultType The type of entity the endpoint returns as output.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: URI, resultType: Class<TResult>) :
        this(referrer, relativeUri, resultType as Type)

    /**
     * Creates a new producer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param resultType The type of entity the endpoint returns as output.
     */
    @Suppress("USELESS_CAST") // Cast selects the Type overload
    constructor(referrer: Endpoint, relativeUri: String, resultType: Class<TResult>) :
        this(referrer, URI(relativeUri), resultType as Type)

    override fun invoke(): TResult =
        execute(Request.Builder().post("".toRequestBody()).uri(uri).build()).use { response ->
            deserialize<TResult>(response.body, resultType)
                ?: throw IllegalStateException("Result not deserializable as ${resultType.displayName}")
        }
}

/**
 * Creates a new producer endpoint, taking the result type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s.
 * @param TResult The type of entity the endpoint returns as output. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TResult> ProducerEndpointImpl(referrer: Endpoint, relativeUri: URI): ProducerEndpointImpl<TResult> =
    ProducerEndpointImpl(referrer, relativeUri, javaTypeOf<TResult>())

/**
 * Creates a new producer endpoint, taking the result type from the reified type argument.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 * @param TResult The type of entity the endpoint returns as output. Can be a generic type such as `List<Contact>`.
 */
inline fun <reified TResult> ProducerEndpointImpl(referrer: Endpoint, relativeUri: String): ProducerEndpointImpl<TResult> =
    ProducerEndpointImpl(referrer, relativeUri, javaTypeOf<TResult>())
