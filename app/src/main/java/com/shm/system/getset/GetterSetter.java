package com.shm.system.getset;

import com.shm.system.entity.ReportsObject;
import com.shm.system.entity.TreatmentObject;
import com.shm.system.entity.UserObject;

import java.util.List;

public class GetterSetter {
    private static UserObject userObject;
    private static TreatmentObject treatmentObject;
    private static ReportsObject reportsObject;
    private static List<TreatmentObject> treatmentObjectList;
    private static boolean shouldReload = false;

    public static ReportsObject getReportsObject() {
        return reportsObject;
    }

    public static void setReportsObject(ReportsObject reportsObject) {
        GetterSetter.reportsObject = reportsObject;
    }

    public static List<TreatmentObject> getTreatmentObjectList() {
        return treatmentObjectList;
    }

    public static void setTreatmentObjectList(List<TreatmentObject> treatmentObjectList) {
        GetterSetter.treatmentObjectList = treatmentObjectList;
    }

    public static boolean isShouldReload() {
        return shouldReload;
    }

    public static void setShouldReload(boolean shouldReload) {
        GetterSetter.shouldReload = shouldReload;
    }

    public static UserObject getUserObject() {
        return userObject;
    }

    public static void setUserObject(UserObject userObject) {
        GetterSetter.userObject = userObject;
    }

    public static TreatmentObject getTreatmentObject() {
        return treatmentObject;
    }

    public static void setTreatmentObject(TreatmentObject treatmentObject) {
        GetterSetter.treatmentObject = treatmentObject;
    }
}
