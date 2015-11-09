package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import java.net.URI;

@SpringUI(path = "")
@Theme("valo")
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        SampleEntryEndpoint entrypoint = new SampleEntryEndpoint(
                URI.create("http://localhost:5893/api"),
                "root", "abc");
        
        setContent(new HorizontalLayout(
                new Button("Test data", x -> addWindow(new TriggerComponent(entrypoint.testData, "Test data"))),
                new Button("Resources", x -> addWindow(new ResourceCollectionComponent(entrypoint.resources))),
                new Button("Resources (paged)", x -> addWindow(new PagedResourceCollectionComponent(entrypoint.resourcesPaged))),
                new Button("Targets", x -> addWindow(new CollectionComponent<>(entrypoint.targets))),
                new Button("Events", x -> addWindow(new EventStreamComponent(entrypoint.events)))
        ));
    }
}
