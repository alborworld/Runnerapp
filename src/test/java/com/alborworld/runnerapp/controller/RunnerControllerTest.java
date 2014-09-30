package com.alborworld.runnerapp.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.alborworld.runnerapp.locking.LockRegistry;
import com.alborworld.runnerapp.model.Runner;
import com.alborworld.runnerapp.model.RunnerModel;
import com.alborworld.runnerapp.utils.RunnerUpdateBuilder;
import com.alborworld.runnerapp.xml.CountryList;
import com.alborworld.runnerapp.xml.RunnerList;
import com.alborworld.runnerapp.xml.RunnerStatus;
import com.alborworld.runnerapp.xml.RunnerUpdate;

public class RunnerControllerTest {

    private RunnerController controller;

    private RunnerModel model;

    private LockRegistry lockRegistry;

    private Lock lock;

    @Before
    public void setup() {
        controller = new RunnerController();

        model = mock(RunnerModel.class);
        controller.setRunnerModel(model);

        lockRegistry = mock(LockRegistry.class);
        controller.setLockRegistry(lockRegistry);

        lock = mock(Lock.class);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                return "";
            }
        }).when(lock).lock();
    }

    @Test
    public void shouldUpdateTheStatusOfARunner() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "One" with 10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                return "Called with arguments: " + args;
            }
        }).when(model).updateRunnerStatus(update);

        when(lockRegistry.getWriteLockFor(new Runner("One", "Australia"))).thenReturn(lock);

        ResponseEntity<Void> response = controller.updateRunnerStatus(update);

        // THEN the HTTP status is 200
        assertEquals("Unexpected HTTP response.", HttpStatus.OK, response.getStatusCode());

        // AND the model is updated only once
        verify(model, times(1)).updateRunnerStatus(update);

        // AND the write lock is used only once
        verify(lockRegistry, times(1)).getWriteLockFor(new Runner("One", "Australia"));
        verify(lock, times(1)).lock();
        verify(lock, times(1)).unlock();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotUpdateWhenRequestObjectIsNull() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update with null request object is performed
        controller.updateRunnerStatus(null);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateWhenRunnerNameIsNull() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "null" in country "Australia" with 10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName(null).withCountry("Australia").withKm(BigInteger.valueOf(10))
                        .build();

        controller.updateRunnerStatus(update);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateWhenRunnerNameIsEmpty() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner with empty in country "Australia" name with 10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("").withCountry("Australia").withKm(BigInteger.valueOf(10)).build();

        controller.updateRunnerStatus(update);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateWhenCountryNameIsNull() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "One" in country null with 10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry(null).withKm(BigInteger.valueOf(10)).build();

        controller.updateRunnerStatus(update);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateWhenCountryNameIsEmpty() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "One" in couontry "" name with 10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("").withKm(BigInteger.valueOf(10)).build();

        controller.updateRunnerStatus(update);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotUpdateWhenNumberOfKmIsNull() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "One" with null km is performed
        RunnerUpdate update = new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(null).build();

        controller.updateRunnerStatus(update);

        // THEN NullPointerException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotUpdateWhenNumberOfKmIsNegative() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN an update of runner "One" with -10 km is performed
        RunnerUpdate update =
                new RunnerUpdateBuilder().withName("One").withCountry("Australia").withKm(BigInteger.valueOf(-10))
                        .build();

        controller.updateRunnerStatus(update);

        // THEN IllegalArgumentException is thrown
    }

    @Test
    public void shouldGetTheStatusOfARunnerWithValidName() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the status of runner "One" is requested
        RunnerStatus expectedStatus = new RunnerStatus();
        when(model.getRunnerStatus("One", "Australia")).thenReturn(expectedStatus);

        when(lockRegistry.getReadLockFor(new Runner("One", "Australia"))).thenReturn(lock);
        RunnerStatus status = controller.getRunnerStatus("One", "Australia").getBody();

        // THEN the expected runner status is returned
        assertEquals("Unexpected runner Status.", expectedStatus, status);

        // AND the total number of status request to the model is one
        verify(model, times(1)).getRunnerStatus("One", "Australia");

        // AND the read lock is used only once
        verify(lockRegistry, times(1)).getReadLockFor(new Runner("One", "Australia"));
        verify(lock, times(1)).lock();
        verify(lock, times(1)).unlock();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetTheStatusOfARunnerWithNullName() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the status of runner with null name is requested
        controller.getRunnerStatus(null, "Australia");

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetTheStatusOfARunnerWithEmptyName() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the status of runner with empty name is requested
        controller.getRunnerStatus("", "Australia");

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetTheStatusOfARunnerWithNullCountry() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the status of runner with null country is requested
        controller.getRunnerStatus("One", null);

        // THEN IllegalArgumentException is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotGetTheStatusOfARunnerWithEmptyCountry() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the status of runner with empty country is requested
        controller.getRunnerStatus("One", "");

        // THEN IllegalArgumentException is thrown
    }

    @Test
    public void shouldGetRunnerList() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the list of runners is requested
        RunnerList expectedRunnerList = new RunnerList();
        when(model.getRunnerList()).thenReturn(expectedRunnerList);

        RunnerList runnerList = controller.getRunnerList().getBody();

        // THEN the expected runner list is returned
        assertEquals("Unexpected runner list.", expectedRunnerList, runnerList);

        // AND the total number of status request to the model is 1
        verify(model, times(1)).getRunnerList();
    }

    @Test
    public void shouldGetCountryList() {
        // GIVEN a controller with mocked model and mocked lock registry

        // WHEN the country list in descending order is requested
        CountryList expectedCountryList = new CountryList();
        when(model.getCountryList(SortCriteria.SORT_BY_DISTANCE, Order.DESCENDING)).thenReturn(expectedCountryList);

        CountryList countryList = controller.getCountryList(SortCriteria.SORT_BY_DISTANCE, Order.DESCENDING).getBody();

        // THEN the expected runner list is returned
        assertEquals("Unexpected country list.", expectedCountryList, countryList);

        // AND the total number of status request to the model is 1
        verify(model, times(1)).getCountryList(SortCriteria.SORT_BY_DISTANCE, Order.DESCENDING);
    }
}