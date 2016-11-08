package com.oneandone.typedrest.vaadin;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

/**
 * Utility methods for Vaadin {@link Grid}s.
 */
public final class GridUtils {

    private GridUtils() {
    }

    /**
     * Adds a row of filter text boxes to a Vaadin {@link Grid}.
     *
     * @param grid The grid to add the filter row to.
     */
    public static void addFilterRow(Grid grid) {
        if (grid.getHeaderRowCount() < 2) {
            grid.appendHeaderRow();
        }
        Grid.HeaderRow headerRow = grid.getHeaderRow(1);

        Container.Indexed container = grid.getContainerDataSource();
        container.getContainerPropertyIds().forEach(pid -> {
            TextField filterField = new TextField();
            filterField.setWidth(100, Sizeable.Unit.PERCENTAGE);
            filterField.setInputPrompt("Filter");
            filterField.addTextChangeListener(event -> {
                ((Container.SimpleFilterable) container).removeContainerFilters(pid);
                if (!event.getText().isEmpty()) {
                    ((Container.Filterable) container).addContainerFilter(new SimpleStringFilter(pid, event.getText(), true, false));
                }
            });
            headerRow.getCell(pid).setComponent(filterField);
        });
    }
}
