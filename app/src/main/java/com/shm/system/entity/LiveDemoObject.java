package com.shm.system.entity;

public class LiveDemoObject {

    private int HR, HUM, OXI, POS, TEMP;

    public LiveDemoObject() {
        this.HR = 0;
        this.HUM = 0;
        this.OXI = 0;
        this.POS = 0;
        this.TEMP = 0;
    }

    public int getHR() {
        return HR;
    }

    public void setHR(int HR) {
        this.HR = HR;
    }

    public int getHUM() {
        return HUM;
    }

    public void setHUM(int HUM) {
        this.HUM = HUM;
    }

    public int getOXI() {
        return OXI;
    }

    public void setOXI(int OXI) {
        this.OXI = OXI;
    }

    public int getTEMP() {
        return TEMP;
    }

    public void setTEMP(int TEMP) {
        this.TEMP = TEMP;
    }

    public int getPOS() {
        return POS;
    }

    public void setPOS(int POS) {
        this.POS = POS;
    }
}
