package com.oneandone.typedrest;

import java.lang.annotation.*;

/**
 * Hides a property in editor UIs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
public @interface EditorHidden {
}
