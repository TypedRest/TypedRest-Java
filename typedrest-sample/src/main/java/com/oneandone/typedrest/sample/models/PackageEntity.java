package com.oneandone.typedrest.sample.models;

import javax.persistence.Id;

/**
 * A software package like an application or a library.
 */
public class PackageEntity {

    private int id;

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PackageEntity() {
    }

    public PackageEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
