package com.alborworld.runnerapp.controller;

import static com.google.common.base.Preconditions.*;
import static org.springframework.util.StringUtils.*;

import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alborworld.runnerapp.locking.LockRegistry;
import com.alborworld.runnerapp.model.Runner;
import com.alborworld.runnerapp.model.RunnerModel;
import com.alborworld.runnerapp.xml.CountryList;
import com.alborworld.runnerapp.xml.RunnerList;
import com.alborworld.runnerapp.xml.RunnerStatus;
import com.alborworld.runnerapp.xml.RunnerUpdate;

@Controller
public class RunnerController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("runnerModel")
    private RunnerModel runnerModel;

    @Autowired
    @Qualifier("lockRegistry")
    private LockRegistry lockRegistry;

    public RunnerController() {
    }

    @RequestMapping(value = "/sendRunnerStatusUpdate", method = RequestMethod.POST,
            headers = "content-type=application/xml")
    @ResponseBody
    public ResponseEntity<Void> updateRunnerStatus(final @RequestBody RunnerUpdate runnerUpdate) {

        validate(runnerUpdate);

        Runner runner = new Runner(runnerUpdate.getName(), runnerUpdate.getCountry());
        Lock lock = lockRegistry.getWriteLockFor(runner);
        lock.lock();

        try {
            runnerModel.updateRunnerStatus(runnerUpdate);
        } finally {
            lock.unlock();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validate(RunnerUpdate runnerUpdate) {
        checkNotNull(runnerUpdate, "ClientUpdate is null");
        checkArgument(hasLength(runnerUpdate.getName()), "Runner name is null or empty");
        checkArgument(hasLength(runnerUpdate.getCountry()), "Country name is null or empty");
        checkNotNull(runnerUpdate.getKm(), "Km is null");
        checkArgument(runnerUpdate.getKm().longValue() >= 0, "Km was %s but expected nonnegative", runnerUpdate.getKm());
    }

    @RequestMapping(value = "/getRunnerStatus", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<RunnerStatus> getRunnerStatus(final @RequestParam("name") String name,
            final @RequestParam("country") String country) {
        logger.info("Requested status of runner \"{}\" in {}.", name, country);

        validate(name, country);

        Lock lock = lockRegistry.getReadLockFor(new Runner(name, country));
        lock.lock();

        RunnerStatus result = null;

        try {
            result = runnerModel.getRunnerStatus(name, country);
        } finally {
            lock.unlock();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void validate(String name, String country) {
        checkArgument(hasLength(name), "Runner name is null or empty");
        checkArgument(hasLength(country), "Country name is null or empty");
    }

    @RequestMapping(value = "/getRunnerList", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<RunnerList> getRunnerList() {

        RunnerList runnerList = runnerModel.getRunnerList();

        return new ResponseEntity<>(runnerList, HttpStatus.OK);
    }

    @RequestMapping(value = "/getCountryList", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CountryList> getCountryList(final @RequestParam(value = "criteria", required = false,
            defaultValue = "SORT_BY_DISTANCE") SortCriteria criteria, final @RequestParam(value = "order",
            required = false, defaultValue = "DESCENDING") Order order) {

        CountryList runnerList = runnerModel.getCountryList(criteria, order);

        return new ResponseEntity<>(runnerList, HttpStatus.OK);
    }

    public void setRunnerModel(RunnerModel runnerModel) {
        this.runnerModel = runnerModel;
    }

    public void setLockRegistry(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }
}