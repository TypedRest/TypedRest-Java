package net.typedrest;

import java.net.URI;

/**
 * Base class for building REST endpoints that represents a collection of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s with pagination
 * support using the HTTP Range header.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 *
 * @deprecated Use {@link CollectionEndpoint} instead.
 */
@Deprecated
public abstract class AbstractPagedCollectionEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends AbstractCollectionEndpoint<TEntity, TElementEndpoint> implements GenericPagedCollectionEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates a new paged collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractPagedCollectionEndpoint(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    /**
     * Creates a new paged collection endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically. Prefix <code>./</code> to append a trailing slash to the
     * parent URI if missing.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractPagedCollectionEndpoint(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }
}
