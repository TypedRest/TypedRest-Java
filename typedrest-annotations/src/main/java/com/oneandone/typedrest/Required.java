package com.oneandone.typedrest;

import java.lang.annotation.*;

/**
 * Marks a property as required (not <code>null</code>).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface Required {
}
