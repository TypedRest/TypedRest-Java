package com.oneandone.typedrest.vaadin;

import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.*;
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

        HeaderRow filterRow = grid.appendHeaderRow();
        for (Object pid : grid.getContainerDataSource().getContainerPropertyIds()) {
            HeaderCell cell = filterRow.getCell(pid);

            TextField filterField = new TextField();
            filterField.setWidth(100, Unit.PERCENTAGE);

            filterField.addTextChangeListener(change -> {
                container.removeContainerFilters(pid);
                if (!change.getText().isEmpty()) {
                    container.addContainerFilter(new SimpleStringFilter(pid, change.getText(), true, false));
                }
            });
            cell.setComponent(filterField);
        }

        setCompositionRoot(grid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<TEntity> getSelectedEntities() {
        return (Collection<TEntity>) grid.getSelectedRows();
    }
}
