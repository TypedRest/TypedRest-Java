package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.ElementEndpoint;
import com.oneandone.typedrest.vaadin.annotations.*;
import com.vaadin.data.Item;
import com.vaadin.ui.UI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Component for updating an existing {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity to update.
 */
public class UpdateElementComponent<TEntity>
        extends AbstractElementComponent<TEntity, ElementEndpoint<TEntity>> {

    /**
     * Creates a new REST element updating component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public UpdateElementComponent(ElementEndpoint<TEntity> endpoint) {
        super(endpoint, endpoint.getEntityType());
        findAndSetEditorFieldGroup(endpoint);
    }

    private void findAndSetEditorFieldGroup(ElementEndpoint<TEntity> endpoint) {
        EditorForm[] editorFieldGroups = endpoint.getEntityType().getAnnotationsByType(EditorForm.class);
        if (editorFieldGroups.length > 0) {
            grid.setEditorEnabled(false);
            grid.addSelectionListener(selectionEvent -> {
                Item item = grid.getContainerDataSource().getItem(grid.getSelectedRow());

                try {
                    EditorFormWindow window = editorFieldGroups[0].formClass().getConstructor().newInstance();
                    window.setItemDataSource(item);

                    window.addCloseListener(closeEvent -> {
                        try {
                            onSave();
                        } catch (IOException | IllegalAccessException | HttpException | OperationNotSupportedException e) {
                            getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
                        }
                    });

                    UI.getCurrent().addWindow(window);

                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
                }
            });
        } else {
            grid.setEditorEnabled(true);
        }
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());

        container.removeAllItems();
        container.addBean(entity);
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        TEntity entity = container.getIdByIndex(0);
        endpoint.update(entity);
    }
}
