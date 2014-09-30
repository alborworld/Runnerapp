package com.alborworld.runnerapp.utils;

import java.math.BigInteger;

import com.alborworld.runnerapp.xml.RunnerUpdate;

public class RunnerUpdateBuilder {

    private final RunnerUpdate runnerUpdate = new RunnerUpdate();

    public RunnerUpdateBuilder withName(String name) {
        runnerUpdate.setName(name);
        return this;
    }

    public RunnerUpdateBuilder withCountry(String country) {
        runnerUpdate.setCountry(country);
        return this;
    }

    public RunnerUpdateBuilder withKm(BigInteger km) {
        runnerUpdate.setKm(km);
        return this;
    }

    public RunnerUpdate build() {
        return runnerUpdate;
    }
}