package com.oneandone.typedrest.vaadin;

import com.oneandone.typedrest.*;
import com.vaadin.data.util.BeanItemContainer;
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

    private final VerticalLayout masterLayout = new VerticalLayout();

    private final BeanItemContainer<TEntity> container;
    protected final Grid grid = new Grid();

    private final Button createButton = new Button("Create", x -> onCreate());
    private final Button deleteButton = new Button("Delete", x -> onDelete((Collection<TEntity>) grid.getSelectedRows()));

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

    /**
     * Controls whether selecting individual elements opens an edit view.
     *
     * @param val Turns the feature on or off.
     */
    private boolean updateEnabled;

    public void setUpdateEnabled(boolean val) {
        updateEnabled = val;
    }

    /**
     * Creates a new REST collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     */
    @SuppressWarnings({"unchecked", "OverridableMethodCallInConstructor"})
    protected AbstractCollectionComponent(TEndpoint endpoint) {
        super(endpoint);
        setCaption(endpoint.getEntityType().getSimpleName() + "s");

        container = new BeanItemContainer<>(endpoint.getEntityType());
        grid.setContainerDataSource(container);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addItemClickListener(x -> {
            if (updateEnabled) {
                onUpdate((TEntity) x.getItemId());
            }
        });

        createButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        deleteButton.addStyleName(ValoTheme.BUTTON_DANGER);
        HorizontalLayout buttonsLayout = new HorizontalLayout(createButton, deleteButton);
        buttonsLayout.setMargin(true);
        buttonsLayout.setSpacing(true);

        masterLayout.addComponents(grid, buttonsLayout);
        masterLayout.setComponentAlignment(buttonsLayout, Alignment.MIDDLE_RIGHT);

        setContent(masterLayout);
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException, HttpException {
        container.removeAllItems();
        container.addAll(endpoint.readAll());
    }

    /**
     * Handler for creating a new entity.
     */
    protected void onCreate() {
        Window elementComponent = buildCreateElementComponent();
        elementComponent.addCloseListener(x -> refresh());
        getUI().addWindow(elementComponent);
    }

    /**
     * Creates a sub-{@link Component} creating a new <code>TEntity</code> in
     * the collection endpoint.
     *
     * @return The new component.
     */
    protected abstract Window buildCreateElementComponent();

    /**
     * Handler for updating an existing entity.
     *
     * @param entity The entity that was clicked.
     */
    protected void onUpdate(TEntity entity) {
        Window elementComponent = buildUpdateElementComponent(endpoint.get(entity));
        elementComponent.addCloseListener(x -> refresh());
        getUI().addWindow(elementComponent);
    }

    /**
     * Creates a sub-{@link Component} for editing an existing
     * <code>TEntity</code> represented by the given element endpoint.
     *
     * @param elementEndpoint The endpoint representing the entity to be
     * updated.
     * @return The new component.
     */
    protected abstract Window buildUpdateElementComponent(TElementEndpoint elementEndpoint);

    /**
     * Handler for deleting a set of existing entities.
     *
     * @param entities The entities to delete.
     */
    protected void onDelete(final Collection<TEntity> entities) {
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
