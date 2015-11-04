package com.oneandone.typedrest.vaadin.annotations;

import com.oneandone.typedrest.vaadin.*;
import com.vaadin.data.fieldgroup.FieldGroup;
import java.lang.annotation.*;

/**
 * Used to set a {@link FieldGroup} for the
 * {@link UpdateElementComponent#grid}'s editor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EditorForm {

    Class<? extends EditorFormWindow> formClass();
}
