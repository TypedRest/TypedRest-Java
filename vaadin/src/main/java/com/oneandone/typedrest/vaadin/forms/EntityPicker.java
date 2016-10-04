package com.oneandone.typedrest.vaadin.forms;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import java.util.Collection;

/**
 * UI component for picking a subset of entities from a set of candidates.
 *
 * Wraps a {@link TwinColSelect} handling the creation of a
 * {@link BeanItemContainer} and adding incremental search.
 *
 * @param <T> The type of entities the picker offers.
 */
public class EntityPicker<T> extends CustomComponent {

    private final TextField filterField = new TextField();
    private final TwinColSelect twinColSelect = new TwinColSelect();
    private final Class<T> entityType;

    private BeanItemContainer<T> container;

    /**
     * Creates a new entity picker.
     *
     * @param entityType The type of entities the picker offers.
     */
    public EntityPicker(Class<T> entityType) {
        this.entityType = entityType;

        filterField.addTextChangeListener(event -> {
            if (container == null) {
                return;
            }

            container.removeAllContainerFilters();
            container.addContainerFilter(new Filter() {
                @Override
                public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
                    return itemId.toString().toLowerCase().contains(event.getText().toLowerCase())
                            || ((Collection) twinColSelect.getValue()).contains(itemId);
                }

                @Override
                public boolean appliesToProperty(Object propertyId) {
                    return true;
                }
            });
        });
        filterField.setInputPrompt("Search");
        filterField.setWidth(50, Unit.PERCENTAGE);

        twinColSelect.setLeftColumnCaption("Available");
        twinColSelect.setSizeFull();
        twinColSelect.setRightColumnCaption("Selected");
        twinColSelect.setNullSelectionAllowed(true);

        VerticalLayout layout = new VerticalLayout(filterField, twinColSelect);
        layout.setSizeFull();
        setCompositionRoot(layout);
    }

    /**
     * Sets a set of candidates for selection.
     *
     * @param candidates The candidates for selection.
     */
    public void setCandidates(Collection<T> candidates) {
        twinColSelect.setContainerDataSource(
                container = new BeanItemContainer<>(entityType, candidates));
    }

    /**
     * Exposes the {@link TwinColSelect} for data binding.
     *
     * @return The {@link TwinColSelect}.
     */
    public Field getField() {
        return twinColSelect;
    }
}
