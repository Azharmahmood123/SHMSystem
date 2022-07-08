package com.shm.system.entity;

public class TreatmentObject {

    private String treatmentID;
    private String treatmentDate;
    private String patientUserID;
    private String patientName;
    private String doctorUserID;
    private String firstAidPersonUserID;
    private String reasonForAccident;
    private String kindOfInjury;
    private String treatmentDetail;
    private String treatedByName;
    private String typeOFDisease;

    public TreatmentObject() {
        this.treatmentID = "";
        this.treatmentDate = "";
        this.patientUserID = "";
        this.patientName = "";
        this.doctorUserID = "";
        this.firstAidPersonUserID = "";
        this.reasonForAccident = "";
        this.kindOfInjury = "";
        this.treatmentDetail = "";
        this.treatedByName = "";
        this.typeOFDisease = "";
    }

    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public String getTreatmentDate() {
        return treatmentDate;
    }

    public void setTreatmentDate(String treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public String getPatientUserID() {
        return patientUserID;
    }

    public void setPatientUserID(String patientUserID) {
        this.patientUserID = patientUserID;
    }

    public String getDoctorUserID() {
        return doctorUserID;
    }

    public void setDoctorUserID(String doctorUserID) {
        this.doctorUserID = doctorUserID;
    }

    public String getFirstAidPersonUserID() {
        return firstAidPersonUserID;
    }

    public void setFirstAidPersonUserID(String firstAidPersonUserID) {
        this.firstAidPersonUserID = firstAidPersonUserID;
    }

    public String getReasonForAccident() {
        return reasonForAccident;
    }

    public void setReasonForAccident(String reasonForAccident) {
        this.reasonForAccident = reasonForAccident;
    }

    public String getKindOfInjury() {
        return kindOfInjury;
    }

    public void setKindOfInjury(String kindOfInjury) {
        this.kindOfInjury = kindOfInjury;
    }

    public String getTreatmentDetail() {
        return treatmentDetail;
    }

    public void setTreatmentDetail(String treatmentDetail) {
        this.treatmentDetail = treatmentDetail;
    }

    public String getTreatedByName() {
        return treatedByName;
    }

    public void setTreatedByName(String treatedByName) {
        this.treatedByName = treatedByName;
    }

    public String getTypeOFDisease() {
        return typeOFDisease;
    }

    public void setTypeOFDisease(String typeOFDisease) {
        this.typeOFDisease = typeOFDisease;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
