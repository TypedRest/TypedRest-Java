package net.typedrest.endpoints

import net.typedrest.errors.*
import net.typedrest.extractCredentials
import net.typedrest.http.*
import net.typedrest.links.*
import net.typedrest.serializers.*
import okhttp3.*
import java.net.URI

/**
 * Represents the top-level URI of an API. Derive from this open class and add your own set of child [Endpoint]s as properties.
 */
open class EntryEndpoint : AbstractEndpoint {
    /**
     * Creates a new entry endpoint.
     *
     * @param uri The base URI of the REST API. Missing trailing slash will be appended automatically.
     * @param httpClient The HTTP client used to communicate with the REST API.
     * @param serializers A list of serializers used for entities received from the server, sorted from most to least preferred. Always uses first for sending to the server.
     * @param errorHandler Handles errors in HTTP responses.
     * @param linkExtractor Detects links in HTTP responses.
     */
    @JvmOverloads
    constructor(
        uri: URI,
        httpClient: OkHttpClient,
        serializers: List<Serializer>,
        errorHandler: ErrorHandler = DefaultErrorHandler(),
        linkExtractor: LinkExtractor = AggregateLinkExtractor(HeaderLinkExtractor(), HalLinkExtractor())
    ) : super(uri, httpClient, serializers, errorHandler, linkExtractor)

    /**
     * Creates a new entry endpoint.
     *
     * @param uri The base URI of the REST API. Missing trailing slash will be appended automatically.
     * @param httpClient The HTTP client used to communicate with the REST API.
     * @param serializer The serializer used for entities sent to and received from the server.
     * @param errorHandler Handles errors in HTTP responses.
     * @param linkExtractor Detects links in HTTP responses.
     */
    @JvmOverloads
    constructor(
        uri: URI,
        httpClient: OkHttpClient,
        serializer: Serializer = KotlinxJsonSerializer(),
        errorHandler: ErrorHandler = DefaultErrorHandler(),
        linkExtractor: LinkExtractor = AggregateLinkExtractor(HeaderLinkExtractor(), HalLinkExtractor())
    ) : super(uri, httpClient, listOf(serializer), errorHandler, linkExtractor)

    /**
     * Creates a new entry endpoint.
     *
     * @param uri The base URI of the REST API.
     * @param credentials Optional HTTP Basic authentication credentials used to authenticate against the REST API.
     * @param serializer The serializer used for entities sent to and received from the server.
     * @param errorHandler Handles errors in HTTP responses.
     * @param linkExtractor Detects links in HTTP responses.
     */
    @JvmOverloads
    constructor(
        uri: URI,
        credentials: HttpCredentials? = null,
        serializer: Serializer = KotlinxJsonSerializer(),
        errorHandler: ErrorHandler = DefaultErrorHandler(),
        linkExtractor: LinkExtractor = AggregateLinkExtractor(HeaderLinkExtractor(), HalLinkExtractor())
    ) : super(
        uri,
        OkHttpClient().withBasicAuth(credentials ?: uri.extractCredentials()),
        listOf(serializer),
        errorHandler,
        linkExtractor
    )

    /**
     * Fetches metadata such as links from the server.
     *
     * @throws AuthenticationException when the server responds with [HttpStatusCode.Unauthorized].
     * @throws AuthorizationException when the server responds with [HttpStatusCode.Forbidden].
     * @throws NotFoundException when the server responds with [HttpStatusCode.NotFound] or [HttpStatusCode.Gone].
     * @throws HttpException for other non-success status codes.
     */
    fun readMeta() =
        execute(Request.Builder().options().uri(uri).build()).close()
}
