package com.oneandone.typedrest;

import java.lang.annotation.*;

/**
 * Marks a String property as not empty or <code>null</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface NotEmpty {
}
