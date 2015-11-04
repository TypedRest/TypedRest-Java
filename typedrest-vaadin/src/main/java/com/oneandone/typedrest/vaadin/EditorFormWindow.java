package com.oneandone.typedrest.vaadin;

import com.vaadin.data.*;
import com.vaadin.data.fieldgroup.*;
import com.vaadin.ui.*;

/**
 * A Window representing a custom Editor for a {@link UpdateElementComponent}.
 * Make sure {@link FieldGroup#commit()} is called before closing the
 * Window to make the {@link UpdateElementComponent} work as expected.
 *
 * @param <TEntity> The type of entity the window represents.
 */
public abstract class EditorFormWindow<TEntity> extends Window {

    protected BeanFieldGroup<TEntity> fieldGroup;

    public EditorFormWindow(BeanFieldGroup<TEntity> fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    /**
     * Sets the item data source for this window.
     *
     * This is method is called by {@link UpdateElementComponent} as soon as an
     * Element was selected.
     *
     * @param item The selected element.
     */
    public abstract void setItemDataSource(Item item);
}
