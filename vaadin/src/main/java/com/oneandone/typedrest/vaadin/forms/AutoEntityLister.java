package com.oneandone.typedrest.vaadin.forms;

import com.vaadin.data.util.filter.*;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.*;

/**
 * An entity lister that uses auto-generated {@link Grid}s.
 *
 * @param <TEntity> The type of entities the list shows.
 */
public class AutoEntityLister<TEntity>
        extends AbstractEntityLister<TEntity> {

    protected final Grid grid = new Grid();

    /**
     * Creates a new entity lister.
     *
     * @param entityType TThe type of entities the list shows.
     */
    @SuppressWarnings({
        "OverridableMethodCallInConstructor", // False positive due to lambda
        "unchecked"}) // Known types in untyped GUI control
    public AutoEntityLister(Class<TEntity> entityType) {
        super(entityType);

        grid.setContainerDataSource(container);
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
    public void scrollToEnd() {
        grid.scrollToEnd();
    }
}
