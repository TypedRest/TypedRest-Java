package net.typedrest.http

import okhttp3.Credentials

/**
 * Represents credentials for HTTP Basic authentication. *
 * @param username The username.
 * @param password The password.
 */
class HttpCredentials(val username: String, val password: String) {
    override fun toString() = Credentials.basic(username, password)
}
