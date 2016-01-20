package com.oneandone.typedrest;

import java.lang.annotation.*;

/**
 * Hides a property in lister UIs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface ListerHidden {
}
