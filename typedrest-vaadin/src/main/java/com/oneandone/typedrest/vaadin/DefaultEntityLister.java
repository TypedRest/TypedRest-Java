package com.oneandone.typedrest.vaadin;

import com.vaadin.ui.*;
import java.util.Collection;

/**
 * An entity lister that uses auto-generated {@link Grid}s.
 *
 * @param <TEntity> The type of entities the list shows.
 */
class DefaultEntityLister<TEntity>
        extends AbstractEntityLister<TEntity> {

    protected final Grid grid = new Grid();

    /**
     * Creates a new entity editor.
     *
     * @param entityType TThe type of entities the list shows.
     */
    @SuppressWarnings({"OverridableMethodCallInConstructor", "unchecked"})
    public DefaultEntityLister(Class<TEntity> entityType) {
        super(entityType);

        grid.setContainerDataSource(container);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addItemClickListener(x -> onClick((TEntity) x.getItemId()));
        grid.setWidth(100, Unit.PERCENTAGE);
        setCompositionRoot(grid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<TEntity> getSelectedEntities() {
        return (Collection<TEntity>) grid.getSelectedRows();
    }
}
