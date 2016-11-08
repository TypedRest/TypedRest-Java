package com.oneandone.typedrest.vaadin.forms;

import com.vaadin.ui.*;
import static com.oneandone.typedrest.vaadin.GridUtils.addFilterRow;

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
        grid.setSizeFull();
        addFilterRow(grid);

        setCompositionRoot(grid);
    }

    @Override
    public void scrollToEnd() {
        grid.scrollToEnd();
    }
}
