package net.typedrest.http

import okhttp3.*
import java.net.URI

/**
 * Returns a new client that sends an `Accept` header listing [mediaTypes] on every request.
 *
 * @param mediaTypes The media types to advertise as acceptable response formats.
 */
fun OkHttpClient.withAccept(mediaTypes: List<MediaType>): OkHttpClient =
    if (mediaTypes.isEmpty()) {
        this
    } else {
        val mediaTypeHeader = mediaTypes.joinToString(separator = ", ")
        this.newBuilder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("Accept", mediaTypeHeader)
                        .build()
                )
            }.build()
    }

/**
 * Returns a new client that sends an `Authorization` header with [credentials] on every request.
 *
 * @param credentials The credentials to include, or null to leave the client unmodified.
 */
fun OkHttpClient.withBasicAuth(credentials: HttpCredentials?): OkHttpClient =
    if (credentials == null) {
        this
    } else {
        this.newBuilder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("Authorization", credentials.toString())
                        .build()
                )
            }.build()
    }

/**
 * Sets the request URL from a [URI].
 *
 * @param uri The URI to use as the request URL.
 */
fun Request.Builder.uri(uri: URI): Request.Builder = this.url(uri.toURL())

/**
 * Sets the request method to `OPTIONS` with no body.
 */
fun Request.Builder.options(): Request.Builder = this.method("OPTIONS", null)
