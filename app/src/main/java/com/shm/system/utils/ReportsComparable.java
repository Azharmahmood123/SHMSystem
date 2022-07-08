package com.shm.system.utils;

import com.shm.system.entity.ReportsObject;
import com.shm.system.entity.TreatmentObject;

import java.util.Comparator;

public class ReportsComparable implements Comparator<ReportsObject> {

    private final boolean asc;

    public ReportsComparable(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(ReportsObject obj1, ReportsObject obj2) {
        return asc ? obj1.getReportDate().compareTo(obj2.getReportDate()) : obj2.getReportDate().compareTo(obj1.getReportDate());
    }

}