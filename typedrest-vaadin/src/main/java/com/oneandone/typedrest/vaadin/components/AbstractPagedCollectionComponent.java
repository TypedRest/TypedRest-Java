package com.oneandone.typedrest.vaadin.components;

import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.*;
import com.oneandone.typedrest.vaadin.forms.DefaultEntityLister;
import com.oneandone.typedrest.vaadin.forms.EntityLister;
import com.vaadin.shared.ui.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

/**
 * Base class for building components operating on an
 * {@link PagedCollectionEndpoint}.
 *
 * @param <TEntity> The type of entity the <code>TEndpoint</code> represents.
 * @param <TEndpoint> The specific type of {@link PagedCollectionEndpoint} to
 * operate on.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} the
 * <code>TEndpoint</code> provides for individual <code>TEntity</code>s.
 */
public abstract class AbstractPagedCollectionComponent<TEntity, TEndpoint extends PagedCollectionEndpoint<TEntity, TElementEndpoint>, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractCollectionComponent<TEntity, TEndpoint, TElementEndpoint> {

    private final Button pageLeftButton = new Button("<");
    private final Button pageRightButton = new Button(">");

    private long pageSize = 5;
    private long currentFrom = 0;
    private long currentTo = pageSize;

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     * @param lister A component for listing entity instances.
     */
    protected AbstractPagedCollectionComponent(TEndpoint endpoint, EventBus eventBus, EntityLister<TEntity> lister) {
        super(endpoint, eventBus, lister);
        pageLeftButton.addClickListener(clickEvent -> {
            currentTo = currentFrom;
            long diff = currentFrom - pageSize;
            currentFrom = diff > 0 ? diff : 0;

            pageRightButton.setEnabled(true);

            refresh();
        });

        pageRightButton.addClickListener(clickEvent -> {
            currentFrom += pageSize;
            currentTo += pageSize;

            pageLeftButton.setEnabled(true);

            refresh();
        });

        pageLeftButton.setEnabled(false);

        pageLeftButton.addStyleName(ValoTheme.BUTTON_TINY);
        pageRightButton.addStyleName(ValoTheme.BUTTON_TINY);

        ComboBox pageSizeComboBox = pageSizeComboBox();

        HorizontalLayout pagingButtonsLayout = new HorizontalLayout(pageLeftButton, pageSizeComboBox, pageRightButton);
        pagingButtonsLayout.setComponentAlignment(pageLeftButton, Alignment.MIDDLE_LEFT);
        pagingButtonsLayout.setComponentAlignment(pageSizeComboBox, Alignment.MIDDLE_CENTER);
        pagingButtonsLayout.setComponentAlignment(pageRightButton, Alignment.MIDDLE_RIGHT);
        pagingButtonsLayout.setMargin(new MarginInfo(true, false, false, false));
        pagingButtonsLayout.setWidth(100, Unit.PERCENTAGE);
        masterLayout.addComponent(pagingButtonsLayout, masterLayout.getComponentIndex(lister));
    }

    /**
     * Creates a new REST paged collection component.
     *
     * @param endpoint The REST endpoint this component operates on.
     * @param eventBus Used to send refresh notifications.
     */
    protected AbstractPagedCollectionComponent(TEndpoint endpoint, EventBus eventBus) {
        this(endpoint, eventBus, new DefaultEntityLister<>(endpoint.getEntityType()));
    }

    private ComboBox pageSizeComboBox() {
        ComboBox pageSizeComboBox = new ComboBox();
        pageSizeComboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
        pageSizeComboBox.addItem(5L);
        pageSizeComboBox.addItem(10L);
        pageSizeComboBox.addItem(15L);
        pageSizeComboBox.addItem(20L);
        pageSizeComboBox.addItem(50L);
        pageSizeComboBox.select(5L);
        pageSizeComboBox.addValueChangeListener(valueChangeEvent -> {
            pageSize = (long) pageSizeComboBox.getValue();
            currentFrom = 0;
            currentTo = pageSize;
            refresh();
        });
        return pageSizeComboBox;
    }

    @Override
    protected void onLoad()
            throws IOException, IllegalArgumentException, IllegalAccessException, FileNotFoundException, OperationNotSupportedException {
        PartialResponse<TEntity> response = endpoint.readRange(currentFrom, currentTo);

        if (response.isEndReached()) {
            pageRightButton.setEnabled(false);
        }

        if (currentFrom <= 0) {
            pageLeftButton.setEnabled(false);
            currentFrom = 0;
        }

        lister.setEntities(response.getElements());
    }
}
