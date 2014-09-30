package com.alborworld.runnerapp.integration;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.alborworld.runnerapp.model.Runner;
import com.alborworld.runnerapp.xml.RunnerStatus;

public class ConcurrentUpdateTest {

    private static final String RUNNERAPP_MAPPING_URL = "/runnerapp/*";
    private static final String CONTEXT_PATH = "/WEB-INF/runnerapp-servlet.xml";

    private static final int THREAD_POOL_SIZE = 10;
    private static final int NUMBER_OF_RUNS = 20;

    private static final Map<Runner, BigInteger> runnerDistanceMap = new HashMap<>();

    static {
        runnerDistanceMap.put(new Runner("James", "Australia"), BigInteger.valueOf(10L));
        runnerDistanceMap.put(new Runner("John", "Unitek Kingdom"), BigInteger.valueOf(15L));
        runnerDistanceMap.put(new Runner("James", "Canada"), BigInteger.valueOf(20L));
        runnerDistanceMap.put(new Runner("Caspar", "The Netherlands"), BigInteger.valueOf(35L));
        runnerDistanceMap.put(new Runner("Marco", "Italy"), BigInteger.valueOf(19L));
    }

    private final String RUNNER_STATUS_UPDATE_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><runnerUpdate xmlns=\"http://com.alborworld/schema/Runner\" "
                    + "name=\"%s\" country=\"%s\" km=\"%d\"/>";

    private Server server;

    private int port;

    @Before
    public void setUp() throws Exception {
        Server server = new ServerFactory(RUNNERAPP_MAPPING_URL, CONTEXT_PATH).createServer();
        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[] { connector });
        server.start();

        // Best practice in testing scenarios is to not hard code the port.
        // That only leads to conflicts when running elsewhere, especially on CI systems that have even a
        // moderate load or variety of projects.
        this.port = connector.getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    // Bombard the service with concurrent updates for many runners
    @Test
    public void shouldRecordAllConcurrentUpdatesForAllRunners() throws InterruptedException, BrokenBarrierException {
        final int nRunners = runnerDistanceMap.size();

        sendConcurrentUpdatesToAllRunners(nRunners);

        assertTotalDistanceRunForAllRunners();
    }

    private void sendConcurrentUpdatesToAllRunners(int nRunners) throws InterruptedException {
        final CyclicBarrier barrier = new CyclicBarrier(THREAD_POOL_SIZE);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        final CountDownLatch untilCompletion = new CountDownLatch(NUMBER_OF_RUNS * nRunners);

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            for (Runner runner : runnerDistanceMap.keySet()) {
                final String name = runner.getName();
                final String country = runner.getCountry();
                final long distancePerRun = runnerDistanceMap.get(runner).longValue();

                executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        barrier.await();
                        sendUpdate(name, country, distancePerRun);
                        untilCompletion.countDown();
                        return null;
                    }
                });
            }
        }

        untilCompletion.await();
    }

    private void assertTotalDistanceRunForAllRunners() {
        for (Runner runner : runnerDistanceMap.keySet()) {
            final String name = runner.getName();
            final String country = runner.getCountry();
            final long distancePerRun = runnerDistanceMap.get(runner).longValue();

            Long expectedTotalDistance = distancePerRun * NUMBER_OF_RUNS;

            RunnerStatus runnerStatus = getRunnerStatus(name, country);

            assertEquals("Unexpected total distance run for " + name + "/" + country + ".",
                    BigInteger.valueOf(expectedTotalDistance), runnerStatus.getTotalKm());
        }
    }

    private RunnerStatus getRunnerStatus(String name, String country) {
        return given().port(port).header("content-type", "application/xml").param("name", name)
                .param("country", country).expect().statusCode(HttpStatus.OK.value()).when()
                .get("/runnerapp/getRunnerStatus").as(RunnerStatus.class);
    }

    private void sendUpdate(String name, String country, long distance) {
        given().port(port).header("content-type", "application/xml").body(b(name, country, distance)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");
    }

    String b(String name, String country, long distance) {
        return String.format(RUNNER_STATUS_UPDATE_TEMPLATE, name, country, distance);
    }
}
