package com.alborworld.runnerapp.model;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alborworld.runnerapp.controller.Order;
import com.alborworld.runnerapp.controller.SortCriteria;
import com.alborworld.runnerapp.xml.Country;
import com.alborworld.runnerapp.xml.CountryList;
import com.alborworld.runnerapp.xml.RunnerList;
import com.alborworld.runnerapp.xml.RunnerStatus;
import com.alborworld.runnerapp.xml.RunnerUpdate;
import com.google.common.collect.Lists;

public class RunnerModel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<Runner, BigInteger> runnerDistanceMap = new HashMap<>();
    private final Map<String, BigInteger> countryDistanceMap = new HashMap<>();

    private final AtomicLong updatesCount = new AtomicLong(0);

    public RunnerModel() {
    }

    public void updateRunnerStatus(RunnerUpdate runnerUpdate) {
        String name = runnerUpdate.getName();
        BigInteger km = runnerUpdate.getKm();
        String country = runnerUpdate.getCountry();

        BigInteger totalDistance = updateRunnerDistance(new Runner(name, country), km);
        updateCountryDistance(country, km);

        logger.info("Runner \"{}\" in {} has just run {} km, for a total of {} km.", name, country, km, totalDistance);

        updatesCount.incrementAndGet();
    }

    private BigInteger updateRunnerDistance(Runner runner, BigInteger km) {
        BigInteger totalDistance = runnerDistanceMap.get(runner);
        totalDistance = (totalDistance == null) ? km : totalDistance.add(km);
        runnerDistanceMap.put(runner, totalDistance);
        return totalDistance;
    }

    private void updateCountryDistance(String country, BigInteger km) {
        BigInteger countryDistance = countryDistanceMap.get(country);
        countryDistance = (countryDistance == null) ? km : countryDistance.add(km);
        countryDistanceMap.put(country, countryDistance);
    }

    public RunnerStatus getRunnerStatus(String name, String country) {
        RunnerStatus runnerStatus = new RunnerStatus();

        Runner runner = new Runner(name, country);
        if (runnerDistanceMap.containsKey(runner)) {
            runnerStatus.setName(name);
            runnerStatus.setCountry(country);
            runnerStatus.setTotalKm(runnerDistanceMap.get(runner));
        } else {
            runnerStatus.setName("N/A");
        }

        return runnerStatus;
    }

    public RunnerList getRunnerList() {
        Collection<Runner> runners = runnerDistanceMap.keySet();
        RunnerList result = new RunnerList();

        for (Runner runner : runners) {
            RunnerStatus runnerStatus = new RunnerStatus();
            runnerStatus.setName(runner.getName());
            runnerStatus.setCountry(runner.getCountry());
            runnerStatus.setTotalKm(runnerDistanceMap.get(runner));
            result.getRunners().add(runnerStatus);
        }

        return result;
    }

    public CountryList getCountryList(final SortCriteria sortCriteria, final Order order) {
        Set<String> countryNames = countryDistanceMap.keySet();

        List<Country> countries = Lists.newArrayList();

        for (String countryName : countryNames) {
            Country country = new Country();
            country.setName(countryName);
            country.setTotalKm(countryDistanceMap.get(countryName));
            countries.add(country);
        }

        if (sortCriteria != SortCriteria.NO_SORT) {
            if (SortCriteria.SORT_BY_NAME.equals(sortCriteria)) {
                Collections.sort(countries, new ComparatorByName(order));
            } else {
                Collections.sort(countries, new ComparatorByDistance(order));
            }
        }

        CountryList countryList = new CountryList();
        countryList.getCountries().addAll(countries);
        return countryList;
    }

    public long getUpdatesCount() {
        return updatesCount.get();
    }
}