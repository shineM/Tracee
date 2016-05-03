package com.zxy.tracee.model;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zxy on 16/3/14.
 */
@Table(name = "table_diary")
public class Diary implements Serializable {

    private int id;

    private Date date;

    private String picPath;

    private String videoPath;

    private double latitude;

    private double longitude;

    private long lastChangeDate;

    private String title;

    private String content;

    public Diary() {

    }

    public Diary(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Diary(int id, Date date, String picPath, String videoPath, double latitude, double longitude, long lastChangeDate, String title, String content) {
        this.id = id;
        this.date = date;
        this.picPath = picPath;
        this.videoPath = videoPath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.lastChangeDate = lastChangeDate;
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
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

    public long getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(long lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    @Override
    public String toString() {
        return "create date:" + date +
                "title" + title +
                "content" + content +
                "picPath" + picPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
