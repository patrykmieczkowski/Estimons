package com.aghacks.estimons.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by lukasz on 24.10.15.
 */
public class DetectedPoke extends RealmObject {

    @PrimaryKey
    private String estimonId;
    private String name;
    private String mac;
    private long lastVisit;

    public DetectedPoke() {
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public long getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public String getEstimonId() {
        return estimonId;
    }

    public void setEstimonId(String estimonId) {
        this.estimonId = estimonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
