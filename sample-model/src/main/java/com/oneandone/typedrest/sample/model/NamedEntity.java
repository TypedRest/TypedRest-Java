package com.oneandone.typedrest.sample.model;

import com.oneandone.typedrest.*;
import lombok.*;

/**
 * An entity with a unique numeric identifier and a human-readable name.
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class NamedEntity {

    @Id
    @EditorHidden
    private long id;

    @NotEmpty
    private String name;

    @Override
    public String toString() {
        return (getName() == null || getName().equals(""))
                ? getClass().getSimpleName() + " " + id
                : getName() + " (" + getId() + ")";
    }
}
