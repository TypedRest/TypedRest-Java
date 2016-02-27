package ${package}.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.net.URI;
import com.oneandone.typedrest.CollectionEndpointImpl;
import ${package}.model.*;
import org.junit.Before;

public class EntitiesEndpointTest extends AbstractEndpointTest {
    private CollectionEndpointImpl<MyEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new CollectionEndpointImpl<>(entryEndpoint, "entities", MyEntity.class);
    }
}
