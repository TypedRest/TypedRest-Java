package com.oneandone.typedrest.vaadin.annotations;

import com.oneandone.typedrest.vaadin.AbstractCollectionComponent;
import java.lang.annotation.*;

/**
 * An annotation marking a field of a model-class to make
 * {@link AbstractCollectionComponent} hide this field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Hidden {
}
