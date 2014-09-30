package com.alborworld.runnerapp.integration;

import static com.jayway.restassured.RestAssured.*;
import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import com.alborworld.runnerapp.xml.Country;
import com.alborworld.runnerapp.xml.CountryList;
import com.alborworld.runnerapp.xml.RunnerList;
import com.alborworld.runnerapp.xml.RunnerStatus;

public class RunnerAppIntegrationTest {

    private static final String RUNNERAPP_MAPPING_URL = "/runnerapp/*";
    private static final String CONTEXT_PATH = "/WEB-INF/runnerapp-servlet.xml";

    private final String RUNNER_STATUS_UPDATE_TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><runnerUpdate xmlns=\"http://com.alborworld/schema/Runner\" "
                    + "name=\"%s\" country=\"%s\" km=\"%d\" />";

    private Server server;

    private int port;

    @Before
    public void setUp() throws Exception {
        Server server = new ServerFactory(RUNNERAPP_MAPPING_URL, CONTEXT_PATH).createServer();
        ServerConnector connector = new ServerConnector(server);
        server.setConnectors(new Connector[] { connector });
        server.start();

        // Best practice in testing scenarios is to not hard code the port.
        // That only leads to conflicts when running parallel tests or elsewhere, especially on CI systems that
        // have even a moderate load or variety of projects.
        this.port = connector.getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    @Test
    public void shouldGetEmptyRunnerStatus() {
        // GIVEN that the application is initialized

        // WHEN the status of "One" is requested
        RunnerStatus runnerStatus =
                given().port(port).header("content-type", "application/xml").param("name", "One")
                        .param("country", "Australia").expect().statusCode(HttpStatus.OK.value()).when()
                        .get("/runnerapp/getRunnerStatus").as(RunnerStatus.class);

        // THEN returned status has name "N/A" and null km
        assertEquals("Unexpected runner name.", "N/A", runnerStatus.getName());
        assertNull("Unexpected number of km.", runnerStatus.getTotalKm());
    }

    @Test
    public void shouldGetBadRequestWithWrongParameter() {
        // GIVEN that the application is initialized

        // WHEN the status of "One" is requested
        // AND a wrong parameter is specified
        given().port(port).header("content-type", "application/xml").param("name", "One").param("wrong", "wrong")
                .expect().statusCode(HttpStatus.BAD_REQUEST.value()).when().get("/runnerapp/getRunnerStatus");

        // THEN it should return a response with HttpStatus.BAD_REQUEST
    }

    @Test
    public void shouldCorrectlyUpdateRunnerStatus() {
        // GIVEN runner "One" in Australia with 10 km
        given().port(port).header("content-type", "application/xml").body(b("One", "Australia", 10)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // WHEN he is updated with other 20 km
        given().port(port).header("content-type", "application/xml").body(b("One", "Australia", 20)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // THEN his total number of ok is 30
        RunnerStatus runnerStatus =
                given().port(port).header("content-type", "application/xml").param("name", "One")
                        .param("country", "Australia").expect().statusCode(HttpStatus.OK.value()).when()
                        .get("/runnerapp/getRunnerStatus").as(RunnerStatus.class);

        assertEquals("Unexpected runner name.", "One", runnerStatus.getName());
        assertEquals("Unexpected country.", "Australia", runnerStatus.getCountry());
        assertEquals("Unexpected number of km.", 30, runnerStatus.getTotalKm().intValue());
    }

    @Test
    public void shouldGetRunnerList() {
        // GIVEN runner "One" in Australia with 10 km
        given().port(port).header("content-type", "application/xml").body(b("One", "Australia", 10)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // AND runner "Two" in the Netherlands with 20 km
        given().port(port).header("content-type", "application/xml").body(b("Two", "The Netherlands", 20)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // AND runner "Three" in Australia with 30 km
        given().port(port).header("content-type", "application/xml").body(b("Three", "Australia", 30)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // WHEN the list of runners is retrieved
        RunnerList runnerList =
                given().port(port).header("content-type", "application/xml").expect().statusCode(HttpStatus.OK.value())
                        .when().get("/runnerapp/getRunnerList").as(RunnerList.class);

        // THEN it contains the current statuses of all the runners
        List<RunnerStatus> runners = runnerList.getRunners();
        assertEquals("Unexpected number of runners.", 3, runners.size());
        for (int i = 0; i < 3; i++) {
            RunnerStatus runnerStatus = runners.get(i);
            if ("One".equals(runnerStatus.getName())) {
                assertEquals("Unexpected country for \"One\".", "Australia", runnerStatus.getCountry());
                assertEquals("Unexpected distance for \"One\".", 10, runnerStatus.getTotalKm().longValue());
            } else if ("Two".equals(runnerStatus.getName())) {
                assertEquals("Unexpected country for \"Two\".", "The Netherlands", runnerStatus.getCountry());
                assertEquals("Unexpected distance for \"Two\".", 20, runnerStatus.getTotalKm().longValue());
            } else if ("Three".equals(runnerStatus.getName())) {
                assertEquals("Unexpected country for \"Three\".", "Australia", runnerStatus.getCountry());
                assertEquals("Unexpected distance for \"Three\".", 30, runnerStatus.getTotalKm().longValue());
            } else {
                fail("Unexpected runner: " + runnerStatus.getName());
            }
        }
    }

    @Test
    public void shouldGetCountryList() {
        // GIVEN runner "One" in Australia with 10 km
        given().port(port).header("content-type", "application/xml").body(b("One", "Australia", 10)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // AND runner "Two" in the Netherlands with 20 km
        given().port(port).header("content-type", "application/xml").body(b("Two", "The Netherlands", 20)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // AND runner "Three" in Australia with 30 km
        given().port(port).header("content-type", "application/xml").body(b("Three", "Australia", 30)).expect()
                .statusCode(HttpStatus.OK.value()).and().post("/runnerapp/sendRunnerStatusUpdate");

        // WHEN the list of runners is retrieved
        // AND no parameters are specified (i.e. default: descending by distance order)
        CountryList countryList =
                given().port(port).header("content-type", "application/xml").expect().statusCode(HttpStatus.OK.value())
                        .when().get("/runnerapp/getCountryList").as(CountryList.class);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the first one is Australia
        Country country = countries.get(0);
        assertEquals("Unespected first country.", "Australia", country.getName());
        assertEquals("Unexpected distance for Australia.", 40, country.getTotalKm().longValue());

        // AND the second one is The Netherlands
        country = countries.get(1);
        assertEquals("Unespected second country.", "The Netherlands", country.getName());
        assertEquals("Unexpected distance for the Netherlands.", 20, country.getTotalKm().longValue());
    }

    String b(String name, String country, int distance) {
        return String.format(RUNNER_STATUS_UPDATE_TEMPLATE, name, country, distance);
    }
}
