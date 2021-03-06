package net.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import net.typedrest.EntryEndpoint;
import com.vaadin.ui.Component;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Base class for building view components operating on an
 * {@link EntryEndpoint}. Use this to build a UI that provides access to an
 * API's top-level functionality.
 *
 * @param <TEndpoint> The specific type of {@link EntryEndpoint} to operate on.
 */
public abstract class AbstractEntryView<TEndpoint extends EntryEndpoint>
        extends AbstractEndpointView<TEndpoint> {

    /**
     * Creates a new REST entry component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    public AbstractEntryView(TEndpoint endpoint) {
        super(endpoint, new EventBus());
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        endpoint.readMeta();

        setCompositionRoot(buildRoot());
    }

    /**
     * Template method called to build the UI composition root after
     * {@link EntryEndpoint#readMeta()} has been called.
     *
     * @return The UI composition root.
     */
    protected abstract Component buildRoot();
}
