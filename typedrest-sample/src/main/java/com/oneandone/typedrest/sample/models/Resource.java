package com.oneandone.typedrest.sample.models;

import lombok.*;

/**
 * A resource that can be deployed to a specific target.
 */
@Getter
@Setter
public class Resource extends NamedEntity {

    /**
     * A target to deploy the resource to.
     */
    private Target target;
}
