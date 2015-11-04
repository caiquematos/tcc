package com.example.caique.sup.Objects;

/**
 * Created by caique on 01/08/15.
 */
public class Coordinator extends Device {
    int network;
    int numOfModules;
    Module [] modules;

    public int getNetwork() {
        return network;
    }

    public void setNetwork(int network) {
        this.network = network;
    }

    public int getNumOfModules() {
        return numOfModules;
    }

    public void setNumOfModules(int numOfModules) {
        this.numOfModules = numOfModules;
    }

    public Module[] getModules() {
        return modules;
    }

    public void setModules(Module[] modules) {
        this.modules = modules;
    }
}
