package com.oneandone.typedrest.sample.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * A specific revision of a {@link Resource}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceRevision extends NamedEntity {
}
