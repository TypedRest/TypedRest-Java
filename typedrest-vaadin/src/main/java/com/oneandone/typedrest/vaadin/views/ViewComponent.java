package com.oneandone.typedrest.vaadin.views;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Window;

/**
 * Base class for building view components that be wrapped in {@link Window}s.
 */
public abstract class ViewComponent
        extends CustomComponent {

    protected Window containingWindow;

    public ViewComponent() {
    }

    /**
     * Opens a child component as a {@link Window}.
     *
     * @param component The child component to open.
     */
    protected void open(ViewComponent component) {
        getUI().addWindow(component.asWindow());
    }

    /**
     * Wraps the control in a {@link Window}.
     *
     * @return The newly created window.
     */
    private Window asWindow() {
        if (isContained()) {
            throw new IllegalStateException("Component can only be wrapped in a window once.");
        }
        containingWindow = new Window(getCaption(), this);
        containingWindow.setWidth(80, Unit.PERCENTAGE);
        containingWindow.setHeight(80, Unit.PERCENTAGE);
        containingWindow.center();
        return containingWindow;
    }

    /**
     * Indicates whether this control has been wrapped in a container.
     *
     * @return <code>true</code> if this control has been wrapped in a
     * container.
     */
    public boolean isContained() {
        return containingWindow != null;
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
}
