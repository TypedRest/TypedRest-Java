package net.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import net.typedrest.*;
import net.typedrest.vaadin.events.*;
import net.typedrest.vaadin.forms.*;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Component for showing or updating an existing element represented by an
 * {@link ElementEndpoint}.
 *
 * @param <TEntity> The type of entity to represent.
 */
public class ElementView<TEntity>
        extends AbstractElementView<TEntity, ElementEndpoint<TEntity>> {

    protected final Button deleteButton = new Button("Delete", x -> delete()) {
        {
            addStyleName(ValoTheme.BUTTON_DANGER);
        }
    };

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    public ElementView(ElementEndpoint<TEntity> endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus, entityForm);

        buttonsLayout.addComponent(deleteButton);
    }

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    public ElementView(ElementEndpoint<TEntity> endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new AutoEntityForm<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        TEntity entity = endpoint.read();
        setCaption(entity.toString());
        entityForm.setEntity(entity);

        endpoint.isSetAllowed().ifPresent(this::setSaveEnabled);
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
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException, Validator.InvalidValueException {
        endpoint.set(entityForm.getEntity());
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
    protected void delete() {
        String question = "Are you sure you want to delete " + getCaption() + "?";
        ConfirmDialog.show(getUI(), question, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                try {
                    onDelete();
                    close();
                } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
                    onError(ex);
                } catch (RuntimeException ex) {
                    // Must explicitly send unhandled exceptions to error handler.
                    // Would otherwise get swallowed silently within callback handler.
                    getUI().getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
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
     * @throws IllegalStateException {@link HttpStatus#SC_CONFLICT}
     * @throws RuntimeException Other non-success status code.
     */
    protected void onDelete()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        endpoint.delete();
        eventBus.post(new ElementDeletedEvent<>(endpoint));
    }
}
