package com.oneandone.typedrest;

import java.net.*;
import rx.*;
import rx.functions.Action2;
import rx.schedulers.Schedulers;
import static rx.util.async.Async.runAsync;

/**
 * Base class for building REST endpoints that represents a stream of
 * <code>TEntity</code>s as <code>TElementEndpoint</code>s.
 *
 * @param <TEntity> The type of entity the endpoint represents.
 * @param <TElementEndpoint> The specific type of {@link ElementEndpoint} to
 * provide for individual <code>TEntity</code>s.
 */
public abstract class AbstractStreamEndpoint<TEntity, TElementEndpoint extends ElementEndpoint<TEntity>>
        extends AbstractPagedCollectionEndpoint<TEntity, TElementEndpoint> implements StreamEndpoint<TEntity, TElementEndpoint> {

    /**
     * Creates a new element stream endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractStreamEndpoint(Endpoint parent, URI relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    /**
     * Creates a new element stream endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s. Missing trailing slash will be appended
     * automatically.
     * @param entityType The type of entity the endpoint represents.
     */
    protected AbstractStreamEndpoint(Endpoint parent, String relativeUri, Class<TEntity> entityType) {
        super(parent, relativeUri, entityType);
    }

    @Override
    public rx.Observable<TEntity> getObservable() {
        return getObservable(0);
    }

    @Override
    public rx.Observable<TEntity> getObservable(long startIndex) {
        return getObservable(startIndex, Schedulers.io());
    }

    /**
     * Provides an observable stream of elements.
     *
     * @param startIndex The index of the first element to return in the stream.
     * Use negative values to start counting from the end of the stream.
     * @param scheduler The scheduler used to run the background thread.
     * @return An observable stream of elements.
     */
    rx.Observable<TEntity> getObservable(final long startIndex, Scheduler scheduler) {
        return runAsync(scheduler, new Action2<rx.Observer<? super TEntity>, Subscription>() {

            @Override
            public void call(rx.Observer<? super TEntity> observer, Subscription subscription) {
                long currentStartIndex = startIndex;
                while (!subscription.isUnsubscribed()) {
                    PartialResponse<TEntity> response;
                    try {
                        response = (currentStartIndex >= 0)
                                ? readRange(currentStartIndex, null)
                                : readRange(null, -currentStartIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        // No new data available yet, keep polling
                        continue;
                    } catch (Throwable error) {
                        observer.onError(error);
                        return;
                    }

                    for (TEntity entity : response.getElements()) {
                        observer.onNext(entity);
                    }

                    if (response.isEndReached()) {
                        observer.onCompleted();
                        return;
                    }

                    // Continue polling for more data
                    currentStartIndex = response.getTo() + 1;
                }
            }
        });
    }
}
