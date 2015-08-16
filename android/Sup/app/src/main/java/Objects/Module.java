package Objects;

import java.io.Serializable;
import java.sql.Time;

/**
 * Created by caique on 01/08/15.
 */
public class Module extends Device implements Serializable{
    int coordinator;
    Time sleepTime;
    int sleepFrequency;
    float packegesFrequency;
    int numOfSamples;
    Sample [] samples;

    public int getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(int coordinator) {
        this.coordinator = coordinator;
    }

    public Time getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Time sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getSleepFrequency() {
        return sleepFrequency;
    }

    public void setSleepFrequency(int sleepFrequency) {
        this.sleepFrequency = sleepFrequency;
    }

    public float getPackegesFrequency() {
        return packegesFrequency;
    }

    public void setPackegesFrequency(float packegesFrequency) {
        this.packegesFrequency = packegesFrequency;
    }

    public int getNumOfSamples() {
        return numOfSamples;
    }

    public void setNumOfSamples(int numOfSamples) {
        this.numOfSamples = numOfSamples;
    }

    public Sample[] getSamples() {
        return samples;
    }

    public void setSamples(Sample[] samples) {
        this.samples = samples;
    }
}
