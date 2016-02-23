package ${package}.client.vaadin.boot;

import com.oneandone.typedrest.vaadin.NotificationErrorHandler;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;

@SpringUI(path = "")
@Theme("valo")
@Push
public class MainUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setErrorHandler(new NotificationErrorHandler());

        //setContent(...);
    }
}
