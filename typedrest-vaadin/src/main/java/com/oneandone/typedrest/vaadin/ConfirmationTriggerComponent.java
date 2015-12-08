package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.TriggerEndpoint;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Component operating on a {@link TriggerEndpoint} that asks a confirmation
 * question before triggering.
 */
public class ConfirmationTriggerComponent extends TriggerComponent {

    private final String question;

    /**
     * Creates a new REST trigger endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param caption A caption for the triggerable action.
     * @param question A question to show the user asking whether to actually
     * trigger the action.
     */
    public ConfirmationTriggerComponent(TriggerEndpoint endpoint, EventBus eventBus, String caption, String question) {
        super(endpoint, eventBus, caption);
        this.question = question;
    }

    @Override
    protected void trigger() {
        ConfirmDialog.show(getUI(), question, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                super.trigger();
            }
        });
    }
}
