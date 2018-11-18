package ${package}.client;

import net.typedrest.*;
import java.net.URI;
import ${package}.model.*;

public class MyEntryEndpoint extends EntryEndpoint {

    /**
     * Creates a new entry point with no authentication.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     */
    public PlannerEntryEndpoint(URI uri) {
        super(uri);
    }

    /**
     * Creates a new entry point using HTTP Basic Auth.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param username The username used to authenticate against the REST
     * interface.
     * @param password The password used to authenticate against the REST
     * interface.
     */
    public PlannerEntryEndpoint(URI uri, String username, String password) {
        super(uri, username, password);
    }

    /**
     * Creates a new entry point using a token for authentication.
     *
     * @param uri The base URI of the REST interface. Missing trailing slash
     * will be appended automatically.
     * @param token The token used to authenticate against the REST
     * interface.
     */
    public PlannerEntryEndpoint(URI uri, String token) {
        super(uri);
        defaultHeaders.add(new BasicHeader("Authorization", "Bearer " + token));
    }

    public CollectionEndpoint<MyEntity> getEntities() {
        return new CollectionEndpointImpl<>(this, "entities", MyEntity.class);
    }
}
