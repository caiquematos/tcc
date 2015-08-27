package com.example.caique.sup.Objects;

/**
 * Created by caique on 01/08/15.
 */
public class Network {
    int id;
    int numOfCoordinators;
    Coordinator [] coordinators;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumOfCoordinators() {
        return numOfCoordinators;
    }

    public void setNumOfCoordinators(int numOfCoordinators) {
        this.numOfCoordinators = numOfCoordinators;
    }

    public Coordinator[] getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(Coordinator[] coordinators) {
        this.coordinators = coordinators;
    }
}
