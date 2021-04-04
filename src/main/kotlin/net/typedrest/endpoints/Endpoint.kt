package net.typedrest.endpoints

import okhttp3.Response

open class Endpoint {
    /**
     * The HTTP URI of the remote resource.
     */
    val uri: URI

    /**
     * The HTTP client used to communicate with the remote resource.
     */
    val httpClient: HttpClient

    /**
     * Controls the serialization of entities sent to and received from the server.
     */
    val serializer: Serializer

    /**
     * Handles errors in responses.
     */
    val errorHandler: ErrorHandler

    /**
     * Extracts links from responses.
     */
    val linkExtractor: LinkExtractor

    /**
     * Creates a new endpoint.
     * @param referrer The endpoint used to navigate to this one. Must be defined except for top-level endpoint.
     * @param uri The HTTP URI of the remote element. May be relative if `referrer` is defined.
     * @param serializer Controls the serialization of entities sent to and received from the server. Taken from `referrer` instead if it is defined.
     * @param errorHandler Handles errors in responses. Taken from `referrer` instead if it is defined.
     * @param linkExtractor Extracts links from responses. Taken from `referrer` instead if it is defined.
     * @param httpClient The HTTP client used to communicate with the remote resource. Taken from `referrer` instead if it is defined.
     */
    constructor(
        referrer: Endpoint?,
        uri: URI | string,
        serializer: Serializer?,
        errorHandler: ErrorHandler?,
        linkExtractor: LinkExtractor?,
        httpClient: HttpClient?) {
        if (referrer) {
            this.uri = (typeof uri === "string") ? referrer.join(uri) : uri
            if (serializer)
                throw new Error("serializer must not be specified if referrer is not specified.")
            this.serializer = referrer.serializer
            if (errorHandler)
                throw new Error("errorHandler must not be specified if referrer is not specified.")
            this.errorHandler = referrer.errorHandler
            if (linkExtractor)
                throw new Error("linkExtractor must not be specified if referrer is not specified.")
            this.linkExtractor = referrer.linkExtractor
            if (httpClient)
                throw new Error("httpClient must not be specified if referrer is not specified.")
            this.httpClient = referrer.httpClient
        } else {
            this.uri = (typeof uri === "string") ? new URI.create(uri) : uri
            if (!serializer)
                throw new Error("serializer must be specified if referrer is not specified.")
            this.serializer = serializer
            if (!errorHandler)
                throw new Error("errorHandler must be specified if referrer is not specified.")
            this.errorHandler = errorHandler
            if (!linkExtractor)
                throw new Error("linkExtractor must be specified if referrer is not specified.")
            this.linkExtractor = linkExtractor
            if (!httpClient)
                throw new Error("httpClient must be specified if referrer is not specified.")
            this.httpClient = httpClient
        }
    }

    /**
     * Resolves a relative URI using this endpoint's URI as the base.
     * @param relativeUri The relative URI to resolve. Prepend `./` to imply a trailing slash in the base URI even if it is missing there.
     */
    protected fun join(relativeUri: string): URI {
        val base = if (relativeUri.startsWith("./")) Endpoint.ensureTrailingSlash(this.uri) else this.uri
        return base.resolve(URI.create(relativeUri))
    }

    /**
     * Adds a trailing slash to the URI if it does not already have one.
     */
    protected static fun ensureTrailingSlash(uri: URI | string): URI {
        let uriString = (typeof uri === "string") ? uri : uri.href
        if (uriString.substr(uriString.length - 1, 1) !== "/")
            uriString += "/"
        return URI.create(uriString)
    }

    /**
     * Sends an HTTP request to this endpoint's URI.
     * Handles various cross-cutting concerns regarding a response message such as discovering links and handling errors.
     * @param method The HTTP method to use.
     * @param headers The HTTP headers to set.
     * @param body The body to send.
     * @throws {@link HttpError}
     */
    protected fun send(method: HttpMethod, headers: Headers?, body: RequestBody?): Response {
        val response = this.httpClient.send(this.uri, method, headers, body)
        this.handle(response)
        return response
    }

    /**
     * Handles various cross-cutting concerns regarding a response message such as discovering links and handling errors.
     * @param response The response to process.
     * @throws {@link HttpError}
     */
    protected fun handle(response: Response) {
        this.links = this.linkExtractor.getLinks(response)
        this.handleCapabilities(response)
        this.errorHandler.handle(response)
    }

    // NOTE: Always replace entire array rather than modifying it to avoid issues.
    private val links: Link[] = []

    // NOTE: Only modified during initial setup of the endpoint.
    private val defaultLinks = Map<string, URI>()
    private val defaultLinkTemplates = Map<string, string>()

    /**
     * Registers one or more default links for a specific relation type.
     * These links are used when no links with this relation type are provided by the server.
     * This should only be called during initial setup of the endpoint.
     * @param rel The relation type of the link to add.
     * @param href The href of the link relative to this endpoint's URI. Leave unspecified to remove any previous entries for the relation type.
     */
    setDefaultLink(rel: string, href?: string) {
        if (href) {
            this.defaultLinks.set(rel, this.join(href))
        } else {
            this.defaultLinks.delete(rel)
        }
    }

    /**
     * Registers a default link template for a specific relation type.
     * This template is used when no template with this relation type is provided by the server.
     * This should only be called during initial setup of the endpoint.
     * @param rel
     * @param href
     */
    setDefaultLinkTemplate(rel: string, href?: string) {
        if (href) {
            this.defaultLinkTemplates.set(rel, href)
        } else {
            this.defaultLinkTemplates.delete(rel)
        }
    }

    /**
     * Resolves all links with a specific relation type. Uses cached data from last response.
     * @param rel The relation type of the links to look for.
     */
    fun getLinks(rel: string): { uri: URI title?: string }[] {
        val links: { uri: URI title?: string }[] = this.links
            .filter(x => !x.templated && x.rel === rel)
            .map(x => {
                return { uri: this.join(x.href), title: x.title }
            })

        val defaultLink = this.defaultLinks.get(rel)
        if (links.length === 0 && defaultLink) {
            links.push({ uri: defaultLink })
        }

        return links
    }

    /**
     * Resolves a single link with a specific relation type. Uses cached data from last response.
     * @param rel The relation type of the link to look for.
     * @throws {@link NotFoundError}: No link with the specified `rel` could be found.
     */
    link(rel: string): URI {
        val links = this.getLinks(rel)

        if (links.length === 0)
            throw new NotFoundError(`No link with rel=${rel} provided by endpoint ${this.uri}.`, 0)

        return links[0].uri
    }

    /**
     * Resolves a link template with a specific relation type. Uses cached data from last response.
     * @param rel The relation type of the link template to look for.
     * @param variables Variables for resolving the template.
     * @throws {@link NotFoundError}: No link template with the specified `rel` could be found.
     */
    linkTemplate(rel: string, variables: { [key: string]: any }): URI {
        return this.join(URI.expand!(this.getLinkTemplate(rel), variables).toString())
    }

    /**
     * Retrieves a link template with a specific relation type. Uses cached data from last response. Prefer {@link linkTemplate} when possible.
     * @param rel The relation type of the link template to look for.
     * @throws {@link NotFoundError}: No link template with the specified `rel` could be found.
     */
    fun getLinkTemplate(rel: string) {
        val template = this.links.find(x => x.templated && x.rel === rel)?.href
            ?? this.defaultLinkTemplates.get(rel)

        if (!template)
            throw new NotFoundError(`No link template with rel=${rel} provided by endpoint ${this.uri}.`, 0)

        return template
    }

    // NOTE: Always replace entire array rather than modifying it to avoid issues.
    private val allowedMethods: HttpMethod[] = []

    /**
     * Handles allowed HTTP methods and other capabilities reported by the server.
     */
    protected fun handleCapabilities(response: Response) {
        val header = response.headers.get(HttpHeader.Allow)
        if (header) {
            this.allowedMethods = header.split(", ") as HttpMethod[]
        }
    }

    /**
     * Shows whether the server has indicated that a specific HTTP method is currently allowed.
     * Uses cached data from last response.
     * @param method The HTTP methods (e.g. GET, POST, ...) to check.
     * @returns `true` if the method is allowed, `false` if the method is not allowed, `undefined` if no request has been sent yet or the server did not specify allowed methods.
     */
    protected fun isMethodAllowed(method: HttpMethod): boolean? {
        if (this.allowedMethods.length === 0)
            return undefined

        return this.allowedMethods.indexOf(method) !== -1
    }
}
