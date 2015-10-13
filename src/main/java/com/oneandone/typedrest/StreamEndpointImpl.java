package com.oneandone.typedrest;

import java.net.*;
import rx.*;

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
    public Observable<TElement> getStream(final long startIndex) {
        return Observable.create(new Observable.OnSubscribe<TElement>() {

            @Override
            public void call(Subscriber<? super TElement> subscriber) {
                long currentStartIndex = startIndex;
                while (true) {
                    PartialResponse<TElement> response;
                    try {
                        response = (currentStartIndex >= 0)
                                ? readPartial(currentStartIndex, null)
                                : readPartial(null, -currentStartIndex);
                    } catch (IndexOutOfBoundsException ex) {
                        // No new data available yet, keep polling
                        continue;
                    } catch (Throwable error) {
                        subscriber.onError(error);
                        return;
                    }

                    for (TElement element : response.getElements()) {
                        subscriber.onNext(element);
                    }

                    if (response.isEndReached()) {
                        subscriber.onCompleted();
                        return;
                    }

                    // Continue polling for more data
                    currentStartIndex = response.getTo() + 1;
                }
            }
        });
    }

    @Override
    public Observable<TElement> getStream() {
        return getStream(0);
    }
}
