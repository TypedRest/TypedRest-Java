package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Common base class for components operating on individual entities.
 *
 * @param <TEntity> The type of entity the component represents.
 * @param <TEndpoint> The type of {@link Endpoint} to operate on.
 */
public abstract class AbstractElementComponent<TEntity, TEndpoint extends Endpoint>
        extends EndpointComponent<TEndpoint> {

    protected final EntityForm<TEntity> entityForm;

    protected final Button saveButton = new Button("Save", x -> {
        try {
            onSave();
            eventBus.post(endpoint);
            close();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            onError(ex);
        }
    });
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton);

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    protected AbstractElementComponent(TEndpoint endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus);

        this.entityForm = entityForm;

        saveButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);

        masterLayout = new VerticalLayout(entityForm, buttonsLayout);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);

        setCompositionRoot(masterLayout);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        entityForm.setReadOnly(readOnly);
        saveButton.setVisible(!readOnly);
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
