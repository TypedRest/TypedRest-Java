package com.oneandone.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.UI;
import org.vaadin.dialogs.ConfirmDialog;
import com.oneandone.typedrest.ActionEndpoint;

/**
 * View component operating on a {@link ActionEndpoint} that asks a
 * confirmation question before triggering.
 */
public class ConfirmationActionView extends ActionView {

    private final String question;

    /**
     * Creates a new REST action endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param caption A caption for the triggerable action.
     * @param question A question to show the user asking whether to actually
     * trigger the action.
     */
    public ConfirmationActionView(ActionEndpoint endpoint, EventBus eventBus, String caption, String question) {
        super(endpoint, eventBus, caption);
        this.question = question;
    }

    @Override
    public void trigger() {
        ConfirmDialog.show(getUI(), question, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                try {
                    super.trigger();
                } catch (RuntimeException ex) {
                    // Must explicitly send unhandled exceptions to error handler.
                    // Would otherwise get swallowed silently within callback handler.
                    UI.getCurrent().getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
                }
            }
        });
    }
}
