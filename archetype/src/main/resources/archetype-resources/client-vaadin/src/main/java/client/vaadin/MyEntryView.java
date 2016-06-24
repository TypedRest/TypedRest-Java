package ${package}.client.vaadin;

import ${package}.client.*;
import com.oneandone.typedrest.vaadin.views.*;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;

public class MyEntryView
        extends AbstractEntryView<MyEntryEndpoint> {

    public MyEntryView(MyEntryEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected Component buildRoot() {
        return new TabSheet(
                new CollectionView<>(endpoint.getEntities(), eventBus));
    }
}
