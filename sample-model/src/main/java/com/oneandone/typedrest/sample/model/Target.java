package com.oneandone.typedrest.sample.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * A specific target to deploy {@link Resource}s to.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Target extends NamedEntity {
}
