package com.oneandone.typedrest.sample.models;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A specific target to deploy {@link Resource}s to.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Target extends NamedEntity {
}
