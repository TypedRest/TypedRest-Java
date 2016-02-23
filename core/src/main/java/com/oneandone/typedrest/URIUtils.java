package com.oneandone.typedrest;

import java.net.*;

/**
 * Provides utility methods for {@link URI}.
 */
public final class URIUtils {

    private URIUtils() {
    }

    /**
     * Adds a trailing slash to the URI if it does not already have one.
     *
     * @param uri The original URI.
     * @return The URI with appended slash.
     */
    public static URI ensureTrailingSlash(URI uri) {
        String asciiString = uri.toASCIIString();
        return asciiString.endsWith("/")
                ? uri
                : URI.create(asciiString + "/");
    }
}
