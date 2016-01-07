package com.oneandone.typedrest;

import com.damnhandy.uri.template.UriTemplate;
import java.net.*;
import java.util.Set;
import org.apache.http.client.fluent.*;

/**
 * REST endpoint, i.e. a remote HTTP resource.
 */
public interface Endpoint {

    /**
     * The REST executor used to communicate with the remote resource.
     *
     * @return The REST executor used to communicate with the remote resource.
     */
    Executor getRest();

    /**
     * The HTTP URI of the remote resource.
     *
     * @return The HTTP URI of the remote resource.
     */
    URI getUri();

    /**
     * Retrieves all links with a specific relation type cached from the last
     * request.
     *
     * @param rel The relation type of the links to look for.
     * @return The hrefs of the links resolved relative to this endpoint's URI.
     */
    Set<URI> getLinks(String rel);

    /**
     * Retrieves a single link with a specific relation type. May be cached from
     * the last request or may be lazily requested.
     *
     * @param rel The relation type of the link to look for.
     * @return The href of the link resolved relative to this endpoint's URI.
     * @throws RuntimeException No link with the specified relation type could
     * be found.
     */
    URI link(String rel);

    /**
     * Retrieves a link template with a specific relation type. May be cached
     * from the last request or may be lazily requested.
     *
     * @param rel The relation type of the template to look for. "-template" is
     * appended implicitly for HTTP Link Headers.
     * @return The link template; <code>null</code> if no link template with the
     * specified relation type could be found.
     */
    UriTemplate linkTemplate(String rel);

    /**
     * Helper method that retrieves a link template with a specific relation
     * type and expands it using a single variable.
     *
     * @param rel The relation type of the template to look for. "-template" is
     * appended implicitly for HTTP Link Headers.
     * @param variableName The name of the variable to insert.
     * @param value The value to insert for the variable.
     * @return The href of the resolved template.
     * @throws RuntimeException No link template with the specified relation
     * type could be found.
     */
    default URI linkTemplateExpanded(String rel, String variableName, Object value) {
        UriTemplate template = linkTemplate(rel);
        if (template == null) {
            throw new RuntimeException("No link template with rel=" + rel + " provided by endpoint " + getUri() + ".");
        }

        return getUri().resolve(template.set(variableName, value).expand());
    }
}
