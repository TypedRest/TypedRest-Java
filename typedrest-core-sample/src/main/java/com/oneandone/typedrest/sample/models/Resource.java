package com.oneandone.typedrest.sample.models;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A resource that can be deployed to a specific target.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource extends NamedEntity {

    /**
     * A target to deploy the resource to.
     */
    private Target target;
}
