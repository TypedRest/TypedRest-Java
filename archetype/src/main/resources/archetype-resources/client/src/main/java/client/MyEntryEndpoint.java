package ${package}.client;

import com.oneandone.typedrest.*;
import java.net.URI;
import ${package}.model.*;

public class MyEntryEndpoint extends EntryEndpoint {

    public MyEntryEndpoint(URI uri, String username, String password) {
        super(uri, username, password);
    }

    public CollectionEndpointImpl<MyEntity> getEntities() {
        return new CollectionEndpointImpl<>(this, "entities", MyEntity.class);
    }
}
