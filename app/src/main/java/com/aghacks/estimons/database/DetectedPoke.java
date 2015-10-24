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
    private int happiness;
    private int health;

    public DetectedPoke() {
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

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
