package com.example.caique.sup.Objects;

import java.sql.Timestamp;

/**
 * Created by caique on 01/08/15.
 */
public class Sample {
    int id;
    float value;
    Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
