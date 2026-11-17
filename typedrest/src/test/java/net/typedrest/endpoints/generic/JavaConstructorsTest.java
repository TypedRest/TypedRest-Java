package net.typedrest.endpoints.generic;

import net.typedrest.MockEntity;
import net.typedrest.endpoints.EntryEndpoint;
import net.typedrest.endpoints.rpc.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Ensures the constructors of typed endpoints can be called from Java with both a {@link Class} and a generic {@link Type}.
 */
class JavaConstructorsTest {
    private final EntryEndpoint entryEndpoint = new EntryEndpoint(URI.create("http://localhost/"));

    private static final Type listOfMockEntity = new ParameterizedType() {
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{MockEntity.class};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };

    @Test
    void elementEndpoint() {
        ElementEndpoint<MockEntity> byClass = new ElementEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
        ElementEndpoint<List<MockEntity>> byType = new ElementEndpointImpl<>(entryEndpoint, URI.create("endpoint"), listOfMockEntity);
        assertEquals(URI.create("http://localhost/endpoint"), byClass.getUri());
        assertEquals(URI.create("http://localhost/endpoint"), byType.getUri());
    }

    @Test
    void collectionEndpoint() {
        CollectionEndpoint<MockEntity> byClass = new CollectionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
        CollectionEndpoint<List<MockEntity>> byType = new CollectionEndpointImpl<>(entryEndpoint, URI.create("endpoint"), listOfMockEntity);
        assertEquals(URI.create("http://localhost/endpoint"), byClass.getUri());
        assertEquals(URI.create("http://localhost/endpoint"), byType.getUri());
    }

    @Test
    void functionEndpoint() {
        FunctionEndpoint<MockEntity, MockEntity> byClass = new FunctionEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class, MockEntity.class);
        FunctionEndpoint<List<MockEntity>, MockEntity> byType = new FunctionEndpointImpl<>(entryEndpoint, "endpoint", listOfMockEntity, MockEntity.class);
        assertEquals(URI.create("http://localhost/endpoint"), byClass.getUri());
        assertEquals(URI.create("http://localhost/endpoint"), byType.getUri());
    }
}
