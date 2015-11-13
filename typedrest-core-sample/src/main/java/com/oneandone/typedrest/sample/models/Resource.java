package com.oneandone.typedrest.sample.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
