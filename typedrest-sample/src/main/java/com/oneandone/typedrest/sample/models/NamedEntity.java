package com.oneandone.typedrest.sample.models;

import javax.persistence.Id;

/**
 * An entity with a unique numeric identifier and a human-readable name.
 */
public abstract class NamedEntity {

    private long id;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}