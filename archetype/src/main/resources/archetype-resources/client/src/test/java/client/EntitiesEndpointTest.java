package ${package}.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.net.URI;
import ${package}.model.*;

public class EntitiesEndpointTest {
    private CollectionEndpointImpl<MyEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CollectionEndpointImpl<>(entryEndpoint, "entities");
    }
}
