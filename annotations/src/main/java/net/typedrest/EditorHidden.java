package net.typedrest;

import java.lang.annotation.*;

/**
 * Hides a property in editor UIs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface EditorHidden {
}
