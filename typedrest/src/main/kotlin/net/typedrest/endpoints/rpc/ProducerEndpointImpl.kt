package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.errors.NotFoundException
import net.typedrest.http.uri
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
    private val resultType: Class<TResult>
) : AbstractRpcEndpoint(referrer, relativeUri), ProducerEndpoint<TResult> {
    /**
     * Creates a new producer endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     * @param resultType The type of entity the endpoint returns as output.
     */
    constructor(referrer: Endpoint, relativeUri: String, resultType: Class<TResult>) :
        this(referrer, URI(relativeUri), resultType)

    override fun invoke(): TResult =
        execute(Request.Builder().post("".toRequestBody()).uri(uri).build()).use { response ->
            deserialize(response.body, resultType)
                ?: throw NotFoundException("Result not deserializable as ${resultType.simpleName}")
        }
}
