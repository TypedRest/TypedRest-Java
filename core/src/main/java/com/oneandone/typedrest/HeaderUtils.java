package com.oneandone.typedrest;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import org.apache.http.HttpResponse;

/**
 * Provides utility methods for HTTP headers.
 */
public final class HeaderUtils {

    private HeaderUtils() {
    }

    /**
     * Returns all Link headers listed in an {@link HttpResponse}.
     *
     * @param response The response to check for Link headers.
     * @return The Link headers found.
     */
    public static Iterable<LinkHeader> getLinkHeaders(HttpResponse response) {
        return stream(response.getHeaders("Link"))
                .flatMap(x -> stream(x.getElements()).map(LinkHeader::new))
                .collect(toList());
    }
}
