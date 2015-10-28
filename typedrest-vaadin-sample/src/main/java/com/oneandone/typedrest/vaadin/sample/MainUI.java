package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.sample.models.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import java.net.URI;

/**
 *
 */
@SpringUI(path = "")
@Theme("valo")
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        UI.getCurrent().setErrorHandler(new NotificationErrorHandler());

        SampleEntryEndpoint entrypoint = new SampleEntryEndpoint(URI.create("http://localhost/api/"));

        // TODO
        //setContent(new VerticalLayout(trigger, element, collection));
    }
}
