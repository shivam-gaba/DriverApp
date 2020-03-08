package com.shivam_gaba;

public class liveLocation {
    public double liveLat;
    public double liveLng;

    public liveLocation(double liveLat, double liveLng) {
        this.liveLat = liveLat;
        this.liveLng = liveLng;
    }

    public double getLiveLat() {
        return liveLat;
    }

    public void setLiveLat(double liveLat) {
        this.liveLat = liveLat;
    }

    public double getLiveLng() {
        return liveLng;
    }

    public void setLiveLng(double liveLng) {
        this.liveLng = liveLng;
    }
}
