package com.shm.system.utils;

import com.shm.system.entity.TreatmentObject;

import java.util.Comparator;

public class PatientComparable implements Comparator<TreatmentObject> {

    private final boolean asc;

    public PatientComparable(boolean asc) {
        this.asc = asc;
    }

    @Override
    public int compare(TreatmentObject obj1, TreatmentObject obj2) {
        return asc ? obj1.getTreatmentDate().compareTo(obj2.getTreatmentDate()) : obj2.getTreatmentDate().compareTo(obj1.getTreatmentDate());
    }

}