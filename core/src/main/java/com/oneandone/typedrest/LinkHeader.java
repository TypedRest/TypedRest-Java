package com.oneandone.typedrest;

import lombok.*;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

/**
 * Represents an HTTP Link header.
 */
public class LinkHeader {

    /**
     * The href/target of the link.
     */
    @Getter
    private final String href;

    /**
     * The relation type of the link.
     */
    @Getter
    private final String rel;

    /**
     * The title of the link (optional).
     */
    @Getter
    private final String title;

    /**
     * Indicates whether the link is an URI Template (RFC 6570).
     */
    @Getter
    private final boolean templated;

    /**
     * Parses a {@link HeaderElement} into an HTTP Link header.
     *
     * @param element The {@link HeaderElement} to parse.
     */
    public LinkHeader(HeaderElement element) {
        href = element.getName().substring(1, element.getName().length() - 1);

        NameValuePair relParameter = element.getParameterByName("rel");
        rel = (relParameter == null) ? null : relParameter.getValue();

        NameValuePair titleParameter = element.getParameterByName("title");
        title = (titleParameter == null) ? null : titleParameter.getValue();

        NameValuePair templatedParameter = element.getParameterByName("templated");
        templated = (templatedParameter != null && templatedParameter.getValue().equals("true"));
    }
}
