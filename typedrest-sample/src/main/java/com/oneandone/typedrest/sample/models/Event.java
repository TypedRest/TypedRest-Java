package com.oneandone.typedrest.sample.models;

import java.time.LocalDateTime;
import lombok.*;

/**
 * A single log event.
 */
@Getter
@Setter
public class Event {

    /**
     * Indicates when this event occurred.
     */
    private LocalDateTime timestamp;
    
    /**
     * A human-readable message describing what happened.
     */
    private String message;
}
