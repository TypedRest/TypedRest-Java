package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.annotations.*;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Common base class for components operating on individual entities.
 *
 * @param <TEntity> The type of entity the component represents.
 * @param <TEndpoint> The type of {@link Endpoint} to operate on.
 */
public abstract class AbstractElementComponent<TEntity, TEndpoint extends Endpoint>
        extends AbstractComponent<TEndpoint> {

    protected final BeanItemContainer<TEntity> container;
    protected final Grid grid = new Grid();
    private final VerticalLayout masterLayout = new VerticalLayout();
    private final Button saveButton = new Button("Save", x -> {
        try {
            onSave();
            close();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
        }
    });
    private final Button cancelButton = new Button("Cancel", x -> close());

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param entityType The type of entity the component represents.
     */
    protected AbstractElementComponent(TEndpoint endpoint, Class<TEntity> entityType) {
        super(endpoint);

        container = new BeanItemContainer<>(entityType);
        grid.setContainerDataSource(container);

        try {
            handleAnnotatedFields(entityType);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(e));
        }

        saveButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);

        masterLayout.addComponents(grid, buttonsLayout);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);

        setContent(masterLayout);
    }

    /**
     * Hides all fields of {@link TEntity} annotated by {@link Hidden} and sets
     * {@link com.vaadin.ui.renderers.Renderer} for all fields, annotated by
     * {@link Renderer}.
     *
     * @param entityType the type of {@link TEntity}.
     */
    private void handleAnnotatedFields(Class<TEntity> entityType)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        for (java.lang.reflect.Field field : entityType.getDeclaredFields()) {

            Grid.Column column = grid.getColumn(field.getName());
            if (column == null) {
                continue;
            }

            if (field.getAnnotationsByType(Hidden.class).length > 0) {
                grid.removeColumn(field.getName());
            } else {
                Renderer[] rendererAnnotations = field.getAnnotationsByType(Renderer.class);
                if (rendererAnnotations.length > 0) {
                    column.setRenderer(rendererAnnotations[0].rendererClass().getConstructor().newInstance());
                }
            }
        }
    }

    /**
     * Handler for saving the input.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws HttpException Other non-success status code.
     */
    protected abstract void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException;
}
