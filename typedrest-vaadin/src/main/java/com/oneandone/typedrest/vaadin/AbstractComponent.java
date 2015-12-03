package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.Endpoint;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.*;
import java.io.*;
import javax.naming.OperationNotSupportedException;
import org.apache.http.*;

/**
 * Common base class for components operating on an {@link Endpoint}.
 *
 * @param <TEndpoint> The specific type of {@link Endpoint} to operate on.
 */
public abstract class AbstractComponent<TEndpoint extends Endpoint>
        extends CustomComponent {

    /**
     * The REST endpoint this component operates on.
     */
    protected final TEndpoint endpoint;

    /**
     * Creates a new REST endpoint component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    protected AbstractComponent(TEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        ErrorHandler handler = super.getErrorHandler();
        if (handler == null) {
            UI ui = getUI();
            return (ui == null) ? null : ui.getErrorHandler();
        } else {
            return handler;
        }
    }

    @Override
    public void attach() {
        super.attach();
        refresh();
    }

    /**
     * Reloads data from the endpoint.
     */
    public final void refresh() {
        try {
            onLoad();
        } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
            getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
        }
    }

    /**
     * Handler for loading data for the endpoint.
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
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
    }

    private Window window;

    /**
     * Wraps the control in a window.
     *
     * @return The newly created window.
     */
    public Window asWindow() {
        if (isWindow()) {
            throw new IllegalStateException("Component can only be converted to a window once.");
        }

        this.setSizeFull();
        window = new Window(getCaption(), this);
        window.setWidth(80, Unit.PERCENTAGE);
        window.setHeight(80, Unit.PERCENTAGE);
        window.center();
        return window;
    }

    /**
     * Indicates whether this control has been wrapped in a window.
     *
     * @return <code>true</code> if this control has been wrapped in a window.
     */
    public boolean isWindow() {
        return window != null;
    }

    @Override
    public void setCaption(String caption) {
        super.setCaption(caption);
        if (isWindow()) {
            window.setCaption(caption);
        }
    }

    /**
     * Closes the containing window.
     */
    public void close() {
        if (isWindow()) {
            window.close();
        }
    }
}
