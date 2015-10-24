package com.aghacks.estimons.util;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by lukasz on 24.10.15.
 */
public class RealmScore extends RealmObject {
    @PrimaryKey
    private String uuid;
    private String score;

    public RealmScore() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
