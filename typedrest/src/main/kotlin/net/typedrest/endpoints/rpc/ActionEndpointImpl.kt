package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.http.uri
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI

/**
 * RPC endpoint that is invoked with no input or output.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the referrer's. Add a "./" prefix here to imply a trailing slash on referrer's URI.
 */
open class ActionEndpointImpl(referrer: Endpoint, relativeUri: URI)
    : AbstractRpcEndpoint(referrer, relativeUri), ActionEndpoint {
    /**
     * Creates a new action endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the referrer's. Add a "./" prefix here to imply a trailing slash on referrer's URI.
     */
    constructor(referrer: Endpoint, relativeUri: String) :
        this(referrer, URI(relativeUri))

    override fun invoke() =
        execute(Request.Builder().post("".toRequestBody()).uri(uri).build()).close()
}
