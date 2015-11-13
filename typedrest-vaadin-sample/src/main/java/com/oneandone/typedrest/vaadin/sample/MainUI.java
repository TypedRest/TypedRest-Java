package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

import java.net.URI;

@SpringUI(path = "")
@Theme("valo")
@Push
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        SampleEntryEndpoint entrypoint = new SampleEntryEndpoint(
                URI.create("http://localhost:5893/api"),
                "root", "abc");
        
        setContent(new TabSheet(
                new ResourceCollectionComponent(entrypoint.resources),
                new PagedResourceCollectionComponent(entrypoint.resourcesPaged),
                new CollectionComponent<>(entrypoint.targets)));
    }
}
