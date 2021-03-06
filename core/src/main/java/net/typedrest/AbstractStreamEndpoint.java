package net.typedrest;

import java.io.IOException;
import java.net.*;
import rx.*;
import rx.schedulers.Schedulers;
import rx.util.async.StoppableObservable;
import static rx.util.async.Async.runAsync;

/**
 * Base class for building REST endpoints that represents a stream of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractStreamEndpoint<TEntity, TElementEndpoint extends Endpoint>
        extends AbstractCollectionEndpoint<TEntity, TElementEndpoint> implements GenericStreamEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates a new element stream endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractStreamEndpoint(Endpoint referrer, URI relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    /**
     * Creates a new element stream endpoint.
     *
     * @param referrer The endpoint used to navigate to this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>referrer</code>'s. Missing trailing slash will be appended
     * automatically. Prefix <code>./</code> to append a trailing slash to the
     * parent URI if missing.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractStreamEndpoint(Endpoint referrer, String relativeUri, Class<TEntity> entityType) {
        super(referrer, relativeUri, entityType);
    }

    @Override
    public StoppableObservable<TEntity> getObservable(long startIndex) {
        return getObservable(startIndex, Schedulers.newThread());
    }

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream.
     * Use negative values to start counting from the end of the stream.
     * @param scheduler The scheduler used to run the background thread.
     * @return An observable stream of elements.
     */
    StoppableObservable<TEntity> getObservable(final long startIndex, Scheduler scheduler) {
        return runAsync(scheduler, (rx.Observer<? super TEntity> observer, Subscription subscription) -> {
            long currentStartIndex = startIndex;
            while (!subscription.isUnsubscribed()) {
                PartialResponse<TEntity> response;
                try {
                    response = (currentStartIndex >= 0)
                            ? readRange(currentStartIndex, null)
                            : readRange(null, -currentStartIndex);
                } catch (IllegalStateException ex) {
                    // No new data available yet, keep polling
                    continue;
                } catch (IOException | IllegalArgumentException | IllegalAccessException error) {
                    observer.onError(error);
                    return;
                }

                response.getElements().stream().forEach(observer::onNext);

                if (response.isEndReached()) {
                    observer.onCompleted();
                    return;
                }

                // Continue polling for more data
                currentStartIndex = response.getTo() + 1;
            }
        });
    }
}
