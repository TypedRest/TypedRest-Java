package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.oneandone.typedrest.BlobEndpoint;
import com.oneandone.typedrest.vaadin.events.BlobUploadEvent;
import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Base class for building view components operating on a {@link BlobEndpoint}.
 */
public abstract class AbstractBlobView extends AbstractEndpointView<BlobEndpoint> {

    protected final Button deleteButton = new Button("Delete", x -> delete()) {
        {
            addStyleName(ValoTheme.BUTTON_DANGER);
        }
    };

    protected AbstractBlobView(BlobEndpoint endpoint, EventBus eventBus) {
        super(endpoint, eventBus);
    }

    @Override
    protected void onLoad() throws IOException, IllegalArgumentException, IllegalAccessException, IllegalStateException {
        try {
            endpoint.probe();
        } catch (IOException | IllegalAccessException | RuntimeException ex) {
            // HTTP OPTIONS server-side implementation is optional
        }

        handleAllowedVerbs();
    }

    /**
     * Enables or disabled buttons based on the "Allow" HTTP header.
     */
    protected void handleAllowedVerbs() {
        endpoint.isDownloadAllowed().ifPresent(this::setDownloadEnabled);
        endpoint.isUploadAllowed().ifPresent(this::setUploadEnabled);
        endpoint.isDeleteAllowed().ifPresent(this::setDeleteEnabled);
    }

    /**
     * Controls whether a download button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public abstract void setDownloadEnabled(boolean val);

    /**
     * Controls whether an upload button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public abstract void setUploadEnabled(boolean val);

    /**
     * Called to upload new blob data to the server.
     */
    protected void upload() {
        try {
            onUpload();
            eventBus.post(new BlobUploadEvent(endpoint));
            Notification.show("Success", "Upload complete", Notification.Type.TRAY_NOTIFICATION);
        } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
            onError(ex);
        }
    }

    /**
     * Handler for uploading the blob.
     *
     * @throws IOException Network communication failed.
     * @throws IllegalArgumentException {@link HttpStatus#SC_BAD_REQUEST}
     * @throws IllegalAccessException {@link HttpStatus#SC_UNAUTHORIZED} or
     * {@link HttpStatus#SC_FORBIDDEN}
     * @throws FileNotFoundException {@link HttpStatus#SC_NOT_FOUND} or
     * {@link HttpStatus#SC_GONE}
     * @throws IllegalStateException The entity has changed since it was last
     * retrieved with {@link #onLoad()}. Your changes were rejected to prevent a
     * lost update.
     * @throws Validator.InvalidValueException The user input is invalid.
     * @throws RuntimeException Other non-success status code.
     */
    protected abstract void onUpload()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException, Validator.InvalidValueException;

    /**
     * Controls whether a delete button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setDeleteEnabled(boolean val) {
        deleteButton.setVisible(val);
    }

    /**
     * Deletes the blob.
     */
    protected void delete() {
        String question = "Are you sure you want to delete the data from the server?";
        ConfirmDialog.show(getUI(), question, new ConfirmDialog.Listener() {
            @Override
            public void onClose(ConfirmDialog cd) {
                if (cd.isConfirmed()) {
                    try {
                        endpoint.delete();
                        close();
                    } catch (IOException | IllegalArgumentException | IllegalAccessException | IllegalStateException ex) {
                        onError(ex);
                    } catch (RuntimeException ex) {
                        // Must explicitly send unhandled exceptions to error handler.
                        // Would otherwise get swallowed silently within callback handler.
                        getUI().getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
                    }
                }
            }
        });
    }
}
