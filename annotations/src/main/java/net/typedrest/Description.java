package net.typedrest;

import java.lang.annotation.*;

/**
 * A human-readable description for a property or class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface Description {

    String value();
}
