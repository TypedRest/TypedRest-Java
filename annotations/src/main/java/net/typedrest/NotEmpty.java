package net.typedrest;

import java.lang.annotation.*;

/**
 * Marks a String property as not empty or <code>null</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface NotEmpty {
}
