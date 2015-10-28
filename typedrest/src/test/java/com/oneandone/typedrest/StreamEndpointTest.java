package com.oneandone.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.oneandone.typedrest.AbstractEndpointTest.jsonMime;
import static java.util.Arrays.asList;
import static org.apache.http.HttpStatus.*;
import org.junit.*;
import rx.*;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class StreamEndpointTest extends AbstractEndpointTest {

    private StreamEndpointImpl<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new StreamEndpointImpl<>(entryPoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testGetObservable() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=0-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 0-1/*")
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=2-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 2-2/3")
                        .withBody("[{\"id\":7,\"name\":\"test3\"}]")));

        TestScheduler scheduler = Schedulers.test();
        Observable<MockEntity> observable = endpoint.getObservable(0, scheduler);

        TestSubscriber<MockEntity> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        scheduler.triggerActions();

        subscriber.assertReceivedOnNext(asList(
                new MockEntity(5, "test1"),
                new MockEntity(6, "test2"),
                new MockEntity(7, "test3")));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void testGetObservableOffset() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=2-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 2-2/3")
                        .withBody("[{\"id\":7,\"name\":\"test3\"}]")));

        TestScheduler scheduler = Schedulers.test();
        Observable<MockEntity> observable = endpoint.getObservable(2, scheduler);

        TestSubscriber<MockEntity> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        scheduler.triggerActions();

        subscriber.assertReceivedOnNext(asList(new MockEntity(7, "test3")));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void testGetObservableOffsetTail() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader("Accept", equalTo(jsonMime))
                .withHeader("Range", equalTo("elements=-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader("Content-Type", jsonMime)
                        .withHeader("Content-Range", "elements 2-2/3")
                        .withBody("[{\"id\":7,\"name\":\"test3\"}]")));

        TestScheduler scheduler = Schedulers.test();
        Observable<MockEntity> observable = endpoint.getObservable(-1, scheduler);

        TestSubscriber<MockEntity> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        scheduler.triggerActions();

        subscriber.assertReceivedOnNext(asList(new MockEntity(7, "test3")));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }
}
