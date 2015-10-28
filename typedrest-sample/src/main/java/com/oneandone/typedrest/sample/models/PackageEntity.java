package com.oneandone.typedrest.sample.models;

import lombok.*;

/**
 * A software package like an application or a library.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageEntity {

    private long id;

    private String name;
}
