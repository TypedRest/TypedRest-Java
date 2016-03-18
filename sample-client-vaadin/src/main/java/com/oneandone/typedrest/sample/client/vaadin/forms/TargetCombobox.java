package com.oneandone.typedrest.sample.client.vaadin.forms;

import com.oneandone.typedrest.sample.model.Target;
import com.vaadin.ui.*;
import java.util.Collection;

/**
 * The combobox managing {@link Resource#target} selection.
 */
class TargetCombobox extends ComboBox {

    public TargetCombobox() {
        super("Target");
        setNullSelectionAllowed(false);
        setConverter(Target.class);
    }

    public void setItems(Collection<Target> targets) {
        removeAllItems();
        addItems(targets);
    }

    /**
     * Returns the selected {@link Target}.
     *
     * @return the selected Target or <code>null</code> if non is selected.
     */
    public Target getSelectedTarget() {
        Object value = getValue();
        if (value != null && value instanceof Target) {
            return (Target) value;
        }
        return null;
    }
}
