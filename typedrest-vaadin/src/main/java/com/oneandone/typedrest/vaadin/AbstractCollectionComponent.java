package com.oneandone.typedrest.vaadin;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.google.gwt.thirdparty.guava.common.eventbus.Subscribe;
import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Base class for building components operating on an
 * {@link CollectionEndpoint}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link CollectionEndpoint} to operate
 * on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractCollectionComponent<TEntity, TEndpoint extends CollectionEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends EndpointComponent<TEndpoint> {

    protected final EntityLister<TEntity> lister;

    private final Button createButton = new Button("Create", x -> onCreateElement());
    protected final Button deleteButton = new Button("Delete", x -> onDeleteElements());
    protected final Button refreshButton = new Button("Refresh", x -> refresh());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(createButton, deleteButton, refreshButton);

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    protected AbstractCollectionComponent(TEndpoint endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus);
        setCaption(endpoint.getEntityType().getSimpleName() + "s");

        this.lister = lister;
        lister.addEntityClickListener(x -> {
            if (openElementEnabled) {
                onOpenElement(x);
            }
        });

        createButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);

        masterLayout = new VerticalLayout(lister, buttonsLayout);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
        setCompositionRoot(masterLayout);
    }

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    protected AbstractCollectionComponent(TEndpoint endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityLister<>(endpoint.getEntityType()));
    }

    /**
     * Controls whether a create button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setCreateEnabled(boolean val) {
        createButton.setVisible(val);
    }

    /**
     * Controls whether a delete button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setDeleteEnabled(boolean val) {
        deleteButton.setVisible(val);
        lister.setSelectionEnabled(val);
    }

    private boolean openElementEnabled = true;

    /**
     * Controls whether selecting individual elements opens an edit view.
     *
     * @param val Turns the feature on or off.
     */
    public void setOpenElementEnabled(boolean val) {
        openElementEnabled = val;
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        lister.setEntities(endpoint.readAll());
    }

    /**
     * Handler for opening an existing element in the collection.
     *
     * @param entity The entity that was clicked.
     */
    protected void onOpenElement(TEntity entity) {
        open(buildElementComponent(endpoint.get(entity)));
    }

    /**
     * Builds an {@link EndpointComponent} for viewing or editing an existing
     * <code>TEntity</code> represented by the given element endpoint.
     *
     * @param elementEndpoint The endpoint representing the entity to be
     * updated.
     * @return The new component.
     */
    protected abstract EndpointComponent buildElementComponent(TElementEndpoint elementEndpoint);

    /**
     * Handler for deleting all selected elements.
     */
    protected void onDeleteElements() {
        Collection<TEntity> entities = lister.getSelectedEntities();
        if (entities.isEmpty()) {
            return;
        }

        String message = "Are you sure you want to delete the following elements?"
                + entities.stream().map(x -> "\n" + x.toString()).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        ConfirmDialog.show(getUI(), message, (ConfirmDialog cd) -> {
            if (cd.isConfirmed()) {
                {
                    try {
                        for (TEntity entity : entities) {
                            endpoint.get(entity).delete();
                        }
                    } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException ex) {
                        onError(ex);
                    }
                };

                refresh();
            }
        });
    }

    /**
     * Handler for creating a new element in the collection.
     */
    protected void onCreateElement() {
        open(buildCreateElementComponent());
    }

    /**
     * Builds an {@link EndpointComponent} for creating a new
     * <code>TEntity</code> in the collection endpoint.
     *
     * @return The new component.
     */
    protected abstract EndpointComponent buildCreateElementComponent();

    @Subscribe
    public void refreshEvent(ElementEndpoint<TEntity> endpoint) {
        if (endpoint.getEntityType() == this.endpoint.getEntityType()) {
            refresh();
        }
    }
}
