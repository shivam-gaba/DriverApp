package com.shivam_gaba;

import java.io.Serializable;

public class marker implements Serializable {
    private double Lat;
    private double lng;
    private double percent;


    public marker(double lat, double lng, double percent) {
        Lat = lat;
        this.lng = lng;
        this.percent = percent;
    }

    public marker()
    {

    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
