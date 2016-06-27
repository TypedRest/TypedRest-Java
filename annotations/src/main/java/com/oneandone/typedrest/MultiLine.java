package com.oneandone.typedrest;

import java.lang.annotation.*;

/**
 * Marks a String property as containing multi-line text.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface MultiLine {
}
