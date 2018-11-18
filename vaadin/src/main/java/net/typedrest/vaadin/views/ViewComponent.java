package net.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Window;

/**
 * Base class for building view components that use an {@link EventBus} and can
 * be wrapped in {@link Window}s.
 */
public abstract class ViewComponent
        extends CustomComponent implements View {

    /**
     * Used to send event between components.
     */
    protected final EventBus eventBus;

    /**
     * Creates a new view component.
     *
     * @param eventBus Used to send event between components.
     */
    protected ViewComponent(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private boolean eventBusRegistered;

    @Override
    public void attach() {
        super.attach();

        if (!eventBusRegistered) {
            eventBus.register(this);
            eventBusRegistered = true;
        }
    }

    @Override
    public void detach() {
        if (eventBusRegistered) {
            eventBus.unregister(this);
            eventBusRegistered = false;
        }

        super.detach();
    }

    /**
     * Opens a child component as a {@link Window}.
     *
     * @param component The child component to open.
     */
    protected void open(ViewComponent component) {
        getUI().addWindow(component.asWindow());
    }

    private Window containingWindow;

    /**
     * Wraps the control in a {@link Window}.
     *
     * @return The newly created window.
     */
    protected Window asWindow() {
        if (containingWindow == null) {
            containingWindow = new Window(getCaption(), this);
            containingWindow.setWidth(80, Unit.PERCENTAGE);
            containingWindow.setHeight(80, Unit.PERCENTAGE);
            containingWindow.center();
        }
        return containingWindow;
    }

    /**
     * Closes the containing {@link Window}.
     */
    protected void close() {
        if (containingWindow != null) {
            containingWindow.close();
        }
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        if (containingWindow != null) {
            containingWindow.setCaption(caption);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
