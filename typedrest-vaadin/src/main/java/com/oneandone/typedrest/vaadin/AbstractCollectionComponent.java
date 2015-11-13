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

    private final Button createButton = new Button("Create", x -> onCreate());
    protected final Button deleteButton = new Button("Delete", x -> onDelete());
    protected final HorizontalLayout buttonsLayout = new HorizontalLayout(createButton, deleteButton);

    protected final VerticalLayout masterLayout;

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param lister A component for listing entity instances.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    protected AbstractCollectionComponent(TEndpoint endpoint, EntityLister<TEntity> lister) {
        super(endpoint);
        setCaption(endpoint.getEntityType().getSimpleName() + "s");

        this.lister = lister;
        lister.addEntityClickListener(x -> {
            if (updateEnabled) {
                onUpdate(x);
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
    }

    private boolean updateEnabled = true;

    /**
     * Controls whether selecting individual elements opens an edit view.
     *
     * @param val Turns the feature on or off.
     */
    public void setUpdateEnabled(boolean val) {
        updateEnabled = val;
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        lister.setEntities(endpoint.readAll());
    }

    /**
     * Handler for creating a new entity.
     */
    protected void onCreate() {
        Window elementWindow = buildCreateElementWindow();
        elementWindow.addCloseListener(x -> refresh());
        getUI().addWindow(elementWindow);
    }

    /**
     * Builds a {@link Window} for creating a new <code>TEntity</code> in the
     * collection endpoint.
     *
     * @return The new component.
     */
    protected abstract Window buildCreateElementWindow();

    /**
     * Handler for updating an existing entity.
     *
     * @param entity The entity that was clicked.
     */
    protected void onUpdate(TEntity entity) {
        Window elementWindow = buildUpdateElementWindow(endpoint.get(entity));
        elementWindow.addCloseListener(x -> refresh());
        getUI().addWindow(elementWindow);
    }

    /**
     * Builds a {@link Window} for editing an existing <code>TEntity</code>
     * represented by the given element endpoint.
     *
     * @param elementEndpoint The endpoint representing the entity to be
     * updated.
     * @return The new component.
     */
    protected abstract Window buildUpdateElementWindow(TElementEndpoint elementEndpoint);

    /**
     * Handler for deleting all selected entities.
     */
    protected void onDelete() {
        Collection<TEntity> entities = lister.getSelectedEntities();

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
}
