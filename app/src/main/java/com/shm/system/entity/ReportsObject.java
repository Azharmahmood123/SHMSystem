package com.shm.system.entity;

public class ReportsObject {
    private String reportID;
    private String patientUserID;
    private String reportTestName;
    private String reportFileName;
    private String reportFilePath;
    private String reportDate;

    public ReportsObject() {
        this.reportID = "";
        this.patientUserID = "";
        this.reportTestName = "";
        this.reportFileName = "";
        this.reportFilePath = "";
        this.reportDate = "";
    }

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getPatientUserID() {
        return patientUserID;
    }

    public void setPatientUserID(String patientUserID) {
        this.patientUserID = patientUserID;
    }

    public String getReportTestName() {
        return reportTestName;
    }

    public void setReportTestName(String reportTestName) {
        this.reportTestName = reportTestName;
    }

    public String getReportFileName() {
        return reportFileName;
    }

    public void setReportFileName(String reportFileName) {
        this.reportFileName = reportFileName;
    }

    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }
}
