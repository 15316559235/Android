package com.example.sc.testmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sc on 2018/4/9.
 */

public class Info implements Serializable{
    private double latitude;
    private double longitude;
    private int imgId;
    private String name;
    private String distance;
    private int good;

    public static List<Info> infos=new ArrayList<Info>();

    static{
        infos.add(new Info(31.299121,121.561207,R.mipmap.school,"上海理工大学","100",20));
    }

    public Info(double latitude, double longitude, int imgId, String name, String distance, int good) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.good = good;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getImfId() {
        return imgId;
    }

    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }

    public int getGood() {
        return good;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setImfId(int imfId) {
        this.imgId = imfId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setGood(int good) {
        this.good = good;
    }
}
