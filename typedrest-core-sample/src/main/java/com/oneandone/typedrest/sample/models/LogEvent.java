package com.oneandone.typedrest.sample.models;

import java.time.LocalDateTime;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * A single log event.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogEvent {

    /**
     * Indicates when this event occurred.
     */
    private LocalDateTime timestamp;
    
    /**
     * A human-readable message describing what happened.
     */
    private String message;
}
