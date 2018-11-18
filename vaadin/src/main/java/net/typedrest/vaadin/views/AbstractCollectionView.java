package net.typedrest.vaadin.views;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.typedrest.*;
import net.typedrest.vaadin.events.ElementEvent;
import net.typedrest.vaadin.forms.AutoEntityLister;
import net.typedrest.vaadin.forms.EntityLister;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Base class for building view components operating on an
 * {@link CollectionEndpoint}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link CollectionEndpoint} to operate
 * on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractCollectionView<TEntity, TEndpoint extends GenericCollectionEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractEndpointView<TEndpoint> {

    protected final EntityLister<TEntity> lister;

    private final Button createButton = new Button("Create", x -> create()) {
        {
            addStyleName(ValoTheme.BUTTON_FRIENDLY);
        }
    };
    protected final Button refreshButton = new Button("Refresh", x -> refresh());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(createButton, refreshButton) {
        {
            setSpacing(true);
        }
    };

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     * @param lister A component for listing entity instances.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    protected AbstractCollectionView(TEndpoint endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus);
        setCaption(endpoint.getEntityType().getSimpleName() + "s");

        this.lister = lister;
        lister.setSizeFull();
        lister.addEntityClickListener(x -> {
            if (openElementEnabled) {
                onOpenElement(x);
            }
        });

        masterLayout = new VerticalLayout(lister, buttonsLayout);
        masterLayout.setExpandRatio(lister, 1);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);
        masterLayout.setMargin(true);
        masterLayout.setSpacing(true);
        masterLayout.setSizeFull();
        setCompositionRoot(masterLayout);
    }

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send event between components.
     */
    protected AbstractCollectionView(TEndpoint endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new AutoEntityLister<>(endpoint.getEntityType()));
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, IllegalStateException {
        lister.setEntities(endpoint.readAll());

        endpoint.isCreateAllowed().ifPresent(this::setCreateEnabled);
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

    /**
     * Handler for opening an existing element in the collection.
     *
     * @param entity The entity that was clicked.
     */
    protected void onOpenElement(TEntity entity) {
        open(buildElementView(endpoint.get(entity)));
    }

    /**
     * Builds an {@link AbstractEndpointView} for viewing or editing an existing
     * <code>TEntity</code> represented by the given element endpoint.
     *
     * @param elementEndpoint The endpoint representing the entity to be
     * updated.
     * @return The new component.
     */
    protected abstract ViewComponent buildElementView(TElementEndpoint elementEndpoint);

    /**
     * Controls whether a create button is shown.
     *
     * @param val Turns the feature on or off.
     */
    public void setCreateEnabled(boolean val) {
        createButton.setVisible(val);
    }

    /**
     * Opens a view for creating a new element in the collection.
     */
    public void create() {
        open(buildCreateElementView());
    }

    /**
     * Builds an {@link AbstractEndpointView} for creating a new
     * <code>TEntity</code> in the collection endpoint.
     *
     * @return The new component.
     */
    protected abstract ViewComponent buildCreateElementView();

    // Refresh when child elements are created or updated
    @Subscribe
    public void handle(ElementEvent<TEntity> message) {
        if (message.getEndpoint().getEntityType() == this.endpoint.getEntityType()) {
            refresh();
        }
    }
}
