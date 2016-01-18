package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.events.*;
import com.oneandone.typedrest.vaadin.forms.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpStatus;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Component for showing or updating an existing element represented by an
 * {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity to represent.
 */
public class ElementComponent<TEntity>
        extends AbstractElementComponent<TEntity, ElementEndpoint<TEntity>> {

    protected final Button deleteButton = new Button("Delete", x -> delete());

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);

        deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
        buttonsLayout.addComponent(deleteButton);
    }

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    public ElementComponent(ElementEndpoint<TEntity> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());
        entityForm.setEntity(entity);

        endpoint.isUpdateAllowed().ifPresent(this::setSaveEnabled);
        endpoint.isDeleteAllowed().ifPresent(this::setDeleteEnabled);
    }

    /**
     * Controls whether a save button is shown and fields are editable.
     *
     * @param val Turns the feature on or off.
     */
    public void setSaveEnabled(boolean val) {
        saveButton.setVisible(val);
        setReadOnly(!val);
        entityForm.setReadOnly(!val);
    }

    @Override
    protected void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        endpoint.update(entityForm.getEntity());
        eventBus.post(new ElementUpdatedEvent<>(endpoint));
    }

    /**
     * Controls whether a delete button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setDeleteEnabled(boolean val) {
        deleteButton.setVisible(val);
    }

    /**
     * Deletes the element.
     */
    public void delete() {
        String question = "Are you sure you want to delete " + getCaption() + "?";
        ConfirmDialog.show(getUI(), question, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                try {
                    onDelete();
                    close();
                } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException ex) {
                    onError(ex);
                } catch (RuntimeException ex) {
                    // Must explicitly send unhandled exceptions to error handler.
                    // Would otherwise get swallowed silently within callback handler.
                    UI.getCurrent().getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
                }
            }
        });
    }

    /**
     * Handler for deleting the element.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws OperationNotSupportedException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    protected void onDelete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        endpoint.delete();
        eventBus.post(new ElementDeletedEvent<>(endpoint));
    }
}
