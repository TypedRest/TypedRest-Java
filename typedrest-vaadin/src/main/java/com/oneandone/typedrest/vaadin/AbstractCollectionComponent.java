package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import javax.naming.OperationNotSupportedException;
import org.apache.http.HttpException;
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
        extends AbstractComponent<TEndpoint> {

    protected final EntityLister<TEntity> lister;

    private final Button createButton = new Button("Create", x -> onCreateElement());
    protected final Button deleteButton = new Button("Delete", x -> onDeleteElements());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(createButton, deleteButton);

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param lister A component for listing entity instances.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor") // False positive due to lambda
    protected AbstractCollectionComponent(TEndpoint endpoint, EntityLister<TEntity> lister) {
        super(endpoint);
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
     */
    protected AbstractCollectionComponent(TEndpoint endpoint) {
        this(endpoint, new DefaultEntityLister<>(endpoint.getEntityType()));
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
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        lister.setEntities(endpoint.readAll());
    }

    /**
     * Handler for opening an existing element in the collection.
     *
     * @param entity The entity that was clicked.
     */
    protected void onOpenElement(TEntity entity) {
        Window elementWindow = buildElementComponent(endpoint.get(entity)).asWindow();
        elementWindow.addCloseListener(x -> refresh());
        getUI().addWindow(elementWindow);
    }

    /**
     * Builds an {@link AbstractComponent} for viewing or editing an existing
     * <code>TEntity</code> represented by the given element endpoint.
     *
     * @param elementEndpoint The endpoint representing the entity to be
     * updated.
     * @return The new component.
     */
    protected abstract AbstractComponent buildElementComponent(TElementEndpoint elementEndpoint);

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
                entities.forEach(x -> {
                    try {
                        endpoint.get(x).delete();
                    } catch (IOException | IllegalArgumentException | IllegalAccessException | OperationNotSupportedException | HttpException ex) {
                        getErrorHandler().error(new com.vaadin.server.ErrorEvent(ex));
                    }
                });

                refresh();
            }
        });
    }

    /**
     * Handler for creating a new element in the collection.
     */
    protected void onCreateElement() {
        Window elementWindow = buildCreateElementComponent().asWindow();
        elementWindow.addCloseListener(x -> refresh());
        getUI().addWindow(elementWindow);
    }

    /**
     * Builds an {@link AbstractComponent} for creating a new
     * <code>TEntity</code> in the collection endpoint.
     *
     * @return The new component.
     */
    protected abstract AbstractComponent buildCreateElementComponent();
}
