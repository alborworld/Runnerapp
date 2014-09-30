package com.alborworld.runnerapp.model;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

import com.alborworld.runnerapp.controller.Order;
import com.alborworld.runnerapp.controller.SortCriteria;
import com.alborworld.runnerapp.utils.RunnerUpdateBuilder;
import com.alborworld.runnerapp.xml.Country;
import com.alborworld.runnerapp.xml.CountryList;
import com.alborworld.runnerapp.xml.RunnerList;
import com.alborworld.runnerapp.xml.RunnerStatus;
import com.alborworld.runnerapp.xml.RunnerUpdate;

public class RunnerModelTest {

    @Test
    public void shouldHaveZeroRunnersAndZeroUpdatesWhenInitialized() {
        // GIVEN an empty model
        RunnerModel model = new RunnerModel();

        // THEN the status of any of the runner has name "N/A"
        RunnerStatus statusRunnerOne = model.getRunnerStatus("One", "Australia");
        assertEquals("Unexpected name of result status of runner \"One\".", "N/A", statusRunnerOne.getName());

        // AND there shouldn't be any runner in the model
        assertEquals("Unexpected number of runners.", 0, model.getRunnerList().getRunners().size());

        // AND the total number of updates is 0
        assertEquals("Unexpected total number of updates.", 0, model.getUpdatesCount());
    }

    @Test
    public void shouldUpdateInitializedModel() {
        // GIVEN a new model
        RunnerModel model = new RunnerModel();

        // WHEN a the status of runner "One" in Australia is updated once with 10 km
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // THEN the Country is Australia
        RunnerStatus status = model.getRunnerStatus("One", "Australia");
        assertEquals("Unexpected country.", "Australia", status.getCountry());

        // AND total number of km is 10
        assertEquals("Unexpected total number of km.", 10, status.getTotalKm().intValue());

        // AND the update counter is 1
        assertEquals("Unexpected number of updates.", 1, model.getUpdatesCount());
    }

    @Test
    public void shouldUpdateExistingRunner() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it has been already updated one with one runner in Australia that has run 10 km
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // WHEN a the status of runner "One" is updated once with another 20 km
        update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // THEN the total number of km is 30
        RunnerStatus status = model.getRunnerStatus("One", "Australia");
        assertEquals("Unexpected total number of km.", 30, status.getTotalKm().intValue());

        // AND the country is till Australia
        assertEquals("Unexpected country.", "Australia", status.getCountry());

        // AND the update counter is 2
        assertEquals("Unexpected number of updates.", 2, model.getUpdatesCount());
    }

    @Test
    public void shouldUpdateMultipleRunnersDifferentNamesAndCountries() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it has the status of one with runner "One" in Australia that has run 10 km
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // WHEN a the status of runner "Two" in The Netherlands is added once (with 20 km)
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // THEN the model has both statuses
        RunnerStatus statusRunnerOne = model.getRunnerStatus("One", "Australia");
        RunnerStatus statusRunnerTwo = model.getRunnerStatus("Two", "The Netherlands");
        assertNotNull("Expected status of runner \"One\", but it isn't there.", statusRunnerOne);
        assertNotNull("Expected status of runner \"Two\", but it isn't there.", statusRunnerTwo);
        assertEquals("Unexpected country for \"One\".", "Australia", statusRunnerOne.getCountry());
        assertEquals("Unexpected country for \"Two\".", "The Netherlands", statusRunnerTwo.getCountry());
        assertEquals("Unexpected total number of km run by \"One\".", 10, statusRunnerOne.getTotalKm().intValue());
        assertEquals("Unexpected total number of km run by \"Two\".", 20, statusRunnerTwo.getTotalKm().intValue());

        // AND the update counter is 2
        assertEquals("Unexpected number of updates.", 2, model.getUpdatesCount());
    }

    @Test
    public void shouldUpdateMultipleRunnersSameNamesDifferentCountries() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it has the status of one with runner "One" in Australia that has run 10 km
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // WHEN a the status of runner "One" in the Netherlands is added once (with 20 km)
        update =
                new RunnerUpdateBuilder().withName("One").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // THEN the model has both statuses
        RunnerStatus statusRunnerOneAustralia = model.getRunnerStatus("One", "Australia");
        RunnerStatus statusRunnerOneTheNetherlands = model.getRunnerStatus("One", "The Netherlands");
        assertNotNull("Expected status of runner \"One\" in Australia, but it isn't there.", statusRunnerOneAustralia);
        assertNotNull("Expected status of runner \"One\" in The Netherlands, but it isn't there.",
                statusRunnerOneTheNetherlands);
        assertEquals("Unexpected country for first runner.", "Australia", statusRunnerOneAustralia.getCountry());
        assertEquals("Unexpected country for second runner.", "The Netherlands",
                statusRunnerOneTheNetherlands.getCountry());
        assertEquals("Unexpected total number of km run by \"One\" in Australia.", 10, statusRunnerOneAustralia
                .getTotalKm().intValue());
        assertEquals("Unexpected total number of km run by \"One\" in The Netherlands.", 20,
                statusRunnerOneTheNetherlands.getTotalKm().intValue());

        // AND the update counter is 2
        assertEquals("Unexpected number of updates.", 2, model.getUpdatesCount());
    }

    @Test
    public void shouldUpdateMultipleRunnersDifferentNamesSameCountry() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it has the status of one with runner "One" in Australia that has run 10 km
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // WHEN a the status of runner "Two" in Australia is added once (with 20 km)
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("Australia").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // THEN the model has both statuses
        RunnerStatus statusRunnerOne = model.getRunnerStatus("One", "Australia");
        RunnerStatus statusRunnerTwo = model.getRunnerStatus("Two", "Australia");
        assertNotNull("Expected status of runner \"One\" in Australia, but it isn't there.", statusRunnerOne);
        assertNotNull("Expected status of runner \"Two\" in Australia, but it isn't there.", statusRunnerTwo);
        assertEquals("Unexpected country for \"One\".", "Australia", statusRunnerOne.getCountry());
        assertEquals("Unexpected country for \"Two\".", "Australia", statusRunnerTwo.getCountry());
        assertEquals("Unexpected total number of km run by \"One\" in Australia.", 10, statusRunnerOne.getTotalKm()
                .intValue());
        assertEquals("Unexpected total number of km run by \"Two\" in Australia.", 20, statusRunnerTwo.getTotalKm()
                .intValue());

        // AND the update counter is 2
        assertEquals("Unexpected number of updates.", 2, model.getUpdatesCount());
    }

    @Test
    public void shouldGetRunnerList() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from Italy
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("Italy").withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of runners is retrieved
        RunnerList runnerList = model.getRunnerList();

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
                assertEquals("Unexpected country for \"Three\".", "Italy", runnerStatus.getCountry());
                assertEquals("Unexpected distance for \"Three\".", 30, runnerStatus.getTotalKm().longValue());
            } else {
                fail("Unexpected runner: " + runnerStatus.getName());
            }
        }
    }

    @Test
    public void shouldGetCountryListDescendingByDistance() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("The Netherlands")
                        .withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of countries is retrieved in descending order by distance
        CountryList countryList = model.getCountryList(SortCriteria.SORT_BY_DISTANCE, Order.DESCENDING);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the first one is The Netherlands
        Country country = countries.get(0);
        assertEquals("Unespected first country.", "The Netherlands", country.getName());
        assertEquals("Unexpected distance for The Netherlands.", 50, country.getTotalKm().longValue());

        // AND the second one is Australia
        country = countries.get(1);
        assertEquals("Unespected second country.", "Australia", country.getName());
        assertEquals("Unexpected distance for Australia.", 10, country.getTotalKm().longValue());
    }

    @Test
    public void shouldGetCountryListAscendingByDistance() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("The Netherlands")
                        .withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of countries is retrieved in descending order by distance
        CountryList countryList = model.getCountryList(SortCriteria.SORT_BY_DISTANCE, Order.ASCENDING);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the first one is Australia
        Country country = countries.get(0);
        assertEquals("Unespected first country.", "Australia", country.getName());
        assertEquals("Unexpected distance for Australia.", 10, country.getTotalKm().longValue());

        // AND the second one is The Netherlands
        country = countries.get(1);
        assertEquals("Unespected second country.", "The Netherlands", country.getName());
        assertEquals("Unexpected distance for The Netherlands.", 50, country.getTotalKm().longValue());
    }

    @Test
    public void shouldGetCountryListDescendingByName() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("The Netherlands")
                        .withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of countries is retrieved in descending order by distance
        CountryList countryList = model.getCountryList(SortCriteria.SORT_BY_NAME, Order.DESCENDING);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the first one is The Netherlands
        Country country = countries.get(0);
        assertEquals("Unespected first country.", "The Netherlands", country.getName());
        assertEquals("Unexpected distance for The Netherlands.", 50, country.getTotalKm().longValue());

        // AND the second one is Australia
        country = countries.get(1);
        assertEquals("Unespected second country.", "Australia", country.getName());
        assertEquals("Unexpected distance for Australia.", 10, country.getTotalKm().longValue());
    }

    @Test
    public void shouldGetCountryListAscendingByName() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("The Netherlands")
                        .withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of countries is retrieved in descending order by distance
        CountryList countryList = model.getCountryList(SortCriteria.SORT_BY_NAME, Order.ASCENDING);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the first one is Australia
        Country country = countries.get(0);
        assertEquals("Unespected first country.", "Australia", country.getName());
        assertEquals("Unexpected distance for Australia.", 10, country.getTotalKm().longValue());

        // AND the second one is The Netherlands
        country = countries.get(1);
        assertEquals("Unespected second country.", "The Netherlands", country.getName());
        assertEquals("Unexpected distance for The Netherlands.", 50, country.getTotalKm().longValue());
    }

    @Test
    public void shouldGetCountryNonSortedList() {
        // GIVEN a model
        RunnerModel model = new RunnerModel();

        // AND it contains runner "One" from Australia
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Two" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Two").withCountry("The Netherlands").withKm(BigInteger.valueOf(20))
                        .build();
        model.updateRunnerStatus(update);

        // AND runner "Three" from The Netherlands
        update =
                new RunnerUpdateBuilder().withName("Three").withCountry("The Netherlands")
                        .withKm(BigInteger.valueOf(30)).build();
        model.updateRunnerStatus(update);

        // WHEN the list of countries is retrieved with no order
        CountryList countryList = model.getCountryList(SortCriteria.NO_SORT, Order.DESCENDING);

        // THEN the number of countries in the result is 2
        List<Country> countries = countryList.getCountries();
        assertEquals("Unexpected number of countries.", 2, countries.size());

        // AND the content is correct
        for (int i = 0; i < 2; i++) {
            Country country = countries.get(i);
            if ("Australia".equals(country.getName())) {
                assertEquals("Unexpected distance for Australia.", 10, country.getTotalKm().longValue());
            } else if ("The Netherlands".equals(country.getName())) {
                assertEquals("Unexpected distance for The Netherlands.", 50, country.getTotalKm().longValue());
            } else {
                fail("Unexpected country: " + country.getName());
            }
        }
    }
}
