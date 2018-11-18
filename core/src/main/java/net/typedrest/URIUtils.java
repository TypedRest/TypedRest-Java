package net.typedrest;

import java.net.*;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

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
    @SneakyThrows
    public static URI ensureTrailingSlash(URI uri) {
        URIBuilder builder = new URIBuilder(uri);
        if (!builder.getPath().endsWith("/")) {
            builder.setPath(builder.getPath() + "/");
        }
        return builder.build();
    }
}
