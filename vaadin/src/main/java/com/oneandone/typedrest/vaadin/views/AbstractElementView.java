package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.EntityForm;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.*;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Base class for building view components that create or update elements.
 *
 * @param <TEntity> The type of entity the component represents.
 * @param <TEndpoint> The type of {@link Endpoint} to operate on.
 */
public abstract class AbstractElementView<TEntity, TEndpoint extends Endpoint>
        extends AbstractEndpointView<TEndpoint> {

    protected final EntityForm<TEntity> entityForm;

    protected final Button saveButton = new Button("Save", x -> save());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton);

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST element component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param entityForm A component for viewing/modifying entity instances.
     */
    protected AbstractElementView(TEndpoint endpoint, EventBus eventBus, EntityForm<TEntity> entityForm) {
        super(endpoint, eventBus);

        this.entityForm = entityForm;

        saveButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);

        masterLayout = new VerticalLayout(entityForm, buttonsLayout);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);

        setHeight(masterLayout.getHeight() * 1.2f, masterLayout.getHeightUnits());
        setCompositionRoot(masterLayout);
    }

    /**
     * Saves the input and closes the {@link Window} (if present).
     */
    public void save() {
        try {
            onSave();
            close();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | Validator.InvalidValueException ex) {
            onError(ex);
        } catch (IllegalStateException ex) {
            // This usually inidicates a "lost update"
            ConfirmDialog.show(getUI(), ex.getLocalizedMessage() + "\nDo you want to refresh this page loosing any changes you have made?", (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                refresh();
            }
        });
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
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link #onLoad()}. Your changes were rejected to
     * prevent a lost update.
     * @throws Validator.InvalidValueException The user input is invalid.
     * @throws RuntimeException Other non-success status code.
     */
    protected abstract void onSave()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException, Validator.InvalidValueException;
}
