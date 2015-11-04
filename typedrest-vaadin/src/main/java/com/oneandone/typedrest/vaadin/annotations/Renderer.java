package com.oneandone.typedrest.vaadin.annotations;

import com.oneandone.typedrest.vaadin.AbstractCollectionComponent;
import java.lang.annotation.*;

/**
 * Used to set a {@link com.vaadin.ui.renderers.Renderer} for the
 * {@link AbstractCollectionComponent#grid}'s editor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Renderer {

    Class<? extends com.vaadin.ui.renderers.Renderer> rendererClass();
}
