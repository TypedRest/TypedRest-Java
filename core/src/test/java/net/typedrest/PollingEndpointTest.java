package net.typedrest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static java.util.Arrays.asList;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.HttpStatus.*;
import org.junit.*;
import rx.*;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import static net.typedrest.AbstractEndpointTest.JSON_MIME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PollingEndpointTest extends AbstractEndpointTest {

    private PollingEndpointImpl<MockEntity> endpoint;

    @Before
    @Override
    public void before() {
        super.before();
        endpoint = new PollingEndpointImpl<>(entryEndpoint, "endpoint", MockEntity.class,
                x -> x.getId() == 3);
        endpoint.setPollingInterval(0);
    }

    @Test
    public void testGetObservable() throws Exception {
        stubFor(get(urlEqualTo("/endpoint"))
                .inScenario("Polling").whenScenarioStateIs(STARTED)
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":1,\"name\":\"test\"}"))
                .willSetStateTo("2"));
        stubFor(get(urlEqualTo("/endpoint"))
                .inScenario("Polling").whenScenarioStateIs("2")
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withBody("{\"id\":2,\"name\":\"test\"}"))
                .willSetStateTo("3"));
        stubFor(get(urlEqualTo("/endpoint"))
                .inScenario("Polling").whenScenarioStateIs("3")
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withHeader(CONTENT_TYPE, JSON_MIME)
                        .withHeader(RETRY_AFTER, "42")
                        .withBody("{\"id\":3,\"name\":\"test\"}")));

        TestScheduler scheduler = Schedulers.test();
        Observable<MockEntity> observable = endpoint.getObservable(scheduler);

        TestSubscriber<MockEntity> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        scheduler.triggerActions();

        subscriber.assertReceivedOnNext(asList(
                new MockEntity(1, "test"),
                new MockEntity(2, "test"),
                new MockEntity(3, "test")));
        subscriber.assertNoErrors();
        subscriber.assertCompleted();

        assertThat(endpoint.getPollingInterval(), is(equalTo(42)));
    }
}
