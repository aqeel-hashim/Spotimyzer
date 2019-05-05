package it.hack.sasninjalabs.spotimyzer.model;

import com.google.firebase.database.Exclude;

/**
 * Created by Aqeel Hashim on 23-Mar-18.
 */

public class Slot {

    private long id;

    private boolean availability;

    private long start;

    private long end;

    private boolean arrived;

    @Exclude
    private Spot spot;

    public Slot() {
    }

    public Slot(long id, boolean availability, long start, long end, boolean arrived, Spot spot) {
        this.id = id;
        this.availability = availability;
        this.start = start;
        this.end = end;
        this.arrived = arrived;
        this.spot = spot;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

}
