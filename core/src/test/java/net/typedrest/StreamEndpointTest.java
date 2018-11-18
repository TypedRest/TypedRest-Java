package net.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;
import rx.*;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import static net.typedrest.AbstractEndpointTest.JSON_MIME;

public class StreamEndpointTest extends AbstractEndpointTest {

    private StreamEndpointImpl<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new StreamEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class);
    }

    @Test
    public void testGetObservable() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=0-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 0-1/*")
                        .withBody("[{\"id\":5,\"name\":\"test1\"},{\"id\":6,\"name\":\"test2\"}]")));

        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=2-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 2-2/3")
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
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=2-"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 2-2/3")
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
    public void testGetObservableTail() throws Exception {
        stubFor(get(urlEqualTo("/endpoint/"))
                .withHeader(ACCEPT, equalTo(JSON_MIME))
                .withHeader(RANGE, equalTo("elements=-1"))
                .willReturn(aResponse()
                        .withStatus(SC_PARTIAL_CONTENT)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(CONTENT_RANGE, "elements 2-2/3")
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
