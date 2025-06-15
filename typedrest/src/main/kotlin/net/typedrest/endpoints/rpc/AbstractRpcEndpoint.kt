package net.typedrest.endpoints.rpc

import net.typedrest.endpoints.*
import net.typedrest.http.*
import okhttp3.Request
import java.net.URI

/**
 * Base class for building RPC endpoints.
 *
 * @param referrer The endpoint used to navigate to this one.
 * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
 */
abstract class AbstractRpcEndpoint(referrer: Endpoint, relativeUri: URI) : AbstractEndpoint(referrer, relativeUri), RpcEndpoint {
    /**
     * Creates a new RPC endpoint with a relative URI.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the [referrer]'s. Add a `./` prefix here to imply a trailing slash on referrer's URI.
     */
    constructor(referrer: Endpoint, relativeUri: String) :
        this(referrer, URI(relativeUri))

    override fun probe() =
        execute(Request.Builder().options().uri(uri).build()).close()

    override val isInvokeAllowed: Boolean?
        get() = isMethodAllowed(HttpMethod.POST)
}
