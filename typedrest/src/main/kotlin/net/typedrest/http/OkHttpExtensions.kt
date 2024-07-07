package net.typedrest.http

import okhttp3.*
import java.net.URI

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

fun Request.Builder.uri(uri: URI): Request.Builder = this.url(uri.toURL())

fun Request.Builder.options(): Request.Builder = this.method("OPTIONS", null)
