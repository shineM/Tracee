package com.zxy.tracee.model;

import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;

/**
 * Created by zxy on 16/5/1.
 */
@Table(name = "route_point")
public class RoutePoint implements Serializable {
    private double latitude;

    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private long date;

    public RoutePoint(double latitude, double longitude, long date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public RoutePoint() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
