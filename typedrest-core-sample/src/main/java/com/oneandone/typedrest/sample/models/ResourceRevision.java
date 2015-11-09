package com.oneandone.typedrest.sample.models;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A specific revision of a {@link Resource}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceRevision extends NamedEntity {
}
