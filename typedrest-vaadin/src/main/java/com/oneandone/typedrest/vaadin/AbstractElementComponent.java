package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.Endpoint;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;

/**
 * Common base class for components operating on individual entities.
 *
 * @param <TEntity> The type of entity the component represents.
 * @param <TEndpoint> The type of {@link Endpoint} to operate on.
 */
public abstract class AbstractElementComponent<TEntity, TEndpoint extends Endpoint>
        extends AbstractComponent<TEndpoint> {

    private final VerticalLayout masterLayout = new VerticalLayout();

    protected final BeanItemContainer<TEntity> container;
    protected final Grid grid = new Grid();

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
