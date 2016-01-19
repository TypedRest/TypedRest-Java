package com.oneandone.typedrest.vaadin.sample;

import com.oneandone.typedrest.vaadin.sample.components.*;
import com.oneandone.typedrest.sample.endpoints.*;
import com.oneandone.typedrest.vaadin.*;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;

@SpringUI(path = "")
@Theme("valo")
@Push
public class MainUI extends UI {

    @Value("${sample.uri}")
    private String sampleUri;
    @Value("${sample.username}")
    private String sampleUsername;
    @Value("${sample.password}")
    private String samplePassword;

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        setContent(new SampleEntryComponent(new SampleEntryEndpoint(
                URI.create(sampleUri), sampleUsername, samplePassword)));
    }
}
