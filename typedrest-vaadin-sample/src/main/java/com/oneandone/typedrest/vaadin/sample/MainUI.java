package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.vaadin.components.CollectionComponent;
import com.google.gwt.thirdparty.guava.common.eventbus.EventBus;
import com.oneandone.typedrest.vaadin.sample.components.*;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import java.net.URI;
import lombok.SneakyThrows;

@SpringUI(path = "")
@Theme("valo")
@Push
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        SampleEntryEndpoint entrypoint = new SampleEntryEndpoint(
                URI.create("http://localhost:5893/"),
                "Root", "abc");
        EventBus eventBus = new EventBus();

        setContent(new TabSheet(
                new ResourceCollectionComponent(entrypoint.getResources(), eventBus),
                new CollectionComponent<>(entrypoint.getTargets(), eventBus)));
    }
}
