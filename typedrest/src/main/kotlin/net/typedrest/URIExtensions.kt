package net.typedrest

import net.typedrest.http.HttpCredentials
import java.net.URI

/**
 * Adds a trailing slash to the URI if it does not already have one.
 */
fun URI.ensureTrailingSlash(): URI =
    if (this.toString().endsWith("/")) {
        this
    } else {
        URI("${this}/")
    }

/**
 * Resolves a relative URI using this URI as the base.
 *
 * @param relativeUri The relative URI to resolve. Prepend "./" to imply a trailing slash in the base URI even if it is missing there.
 */
fun URI.join(relativeUri: String): URI =
    if (relativeUri.startsWith("./")) {
        this.ensureTrailingSlash()
    } else {
        this
    }.resolve(relativeUri)

/**
 * Resolves a relative URI using this URI as the base.
 *
 * @param relativeUri The relative URI to resolve. Prepend "./" to imply a trailing slash in the base URI even if it is missing there.
 */
fun URI.join(relativeUri: URI): URI =
    if (relativeUri.toString().startsWith("./")) {
        this.ensureTrailingSlash()
    } else {
        this
    }.resolve(relativeUri)

/**
 * Extracts credentials from user info in URI if set.
 */
fun URI.extractCredentials(): HttpCredentials? =
    this.userInfo?.split(':')?.let { (username, password) -> HttpCredentials(username, password) }
