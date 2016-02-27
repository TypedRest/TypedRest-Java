package ${package}.client.vaadin.boot;

import ${package}.client.*;
import ${package}.client.vaadin.views.*;
import com.oneandone.typedrest.vaadin.NotificationErrorHandler;
import com.vaadin.annotations.*;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;

@SpringUI(path = "")
@Theme("valo")
@Push
public class MainUI extends UI {

    @Value("${api.uri}")
    private URI apiUri;
    @Value("${api.username}")
    private String apiUserName;
    @Value("${api.password}")
    private String apiPassword;

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        setContent(new MyEntryView(
                new MyEntryEndpoint(apiUri, apiUserName, apiPassword)));
    }
}
