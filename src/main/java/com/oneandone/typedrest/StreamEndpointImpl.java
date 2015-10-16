package com.oneandone.typedrest;

import java.net.*;
import rx.*;
import rx.functions.Action2;
import rx.schedulers.Schedulers;
import static rx.util.async.Async.runAsync;

/**
 * REST endpoint that represents a stream of entities. Uses the HTTP Range
 * header and long polling.
 *
 * @param <TElement> The type of elements the endpoint represents.
 */
public class StreamEndpointImpl<TElement>
        extends PaginationEndpointImpl<TElement>
        implements StreamEndpoint<TElement> {

    /**
     * Creates a new stream endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param elementType The type of elements the endpoint represents.
     */
    public StreamEndpointImpl(Endpoint parent, URI relativeUri, Class<TElement> elementType) {
        super(parent, relativeUri, elementType);
    }

    /**
     * Creates a new stream endpoint.
     *
     * @param parent The parent endpoint containing this one.
     * @param relativeUri The URI of this endpoint relative to the
     * <code>parent</code>'s.
     * @param elementType The type of elements the endpoint represents.
     */
    public StreamEndpointImpl(Endpoint parent, String relativeUri, Class<TElement> elementType) {
        super(parent, relativeUri, elementType);
    }

    @Override
    public Observable<TElement> getObservable() {
        return getObservable(0);
    }

    @Override
    public Observable<TElement> getObservable(long startIndex) {
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
    Observable<TElement> getObservable(final long startIndex, Scheduler scheduler) {
        return runAsync(scheduler, new Action2<Observer<? super TElement>, Subscription>() {

            @Override
            public void call(Observer<? super TElement> observer, Subscription subscription) {
                long currentStartIndex = startIndex;
                while (!subscription.isUnsubscribed()) {
                    PartialResponse<TElement> response;
                    try {
                        response = (currentStartIndex >= 0)
                                ? readPartial(currentStartIndex, null)
                                : readPartial(null, -currentStartIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        // No new data available yet, keep polling
                        continue;
                    } catch (Throwable error) {
                        observer.onError(error);
                        return;
                    }

                    for (TElement element : response.getElements()) {
                        observer.onNext(element);
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
