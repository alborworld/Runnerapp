package com.alborworld.runnerapp.model;

import java.util.Comparator;

import com.alborworld.runnerapp.controller.Order;
import com.alborworld.runnerapp.xml.Country;

public class ComparatorByDistance implements Comparator<Country> {

    private final Order order;

    public ComparatorByDistance(Order order) {
        this.order = order;
    }

    @Override
    public int compare(Country c1, Country c2) {
        int cmp = 0;

        cmp = c1.getTotalKm().compareTo(c2.getTotalKm());
        if (Order.DESCENDING.equals(order)) {
            cmp *= -1;
        }

        return cmp;
    }
}
