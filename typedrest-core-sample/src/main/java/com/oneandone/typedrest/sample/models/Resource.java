package com.oneandone.typedrest.sample.models;

import com.fasterxml.jackson.annotation.*;
import com.oneandone.typedrest.*;
import java.util.Set;
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
    @ListerHidden
    @Required
    private Target target;

    /**
     * All dependencies of this resource that need to be deployed to the same
     * host.
     */
    @ListerHidden
    @EditorHidden
    private Set<Resource> dependencies;
}
