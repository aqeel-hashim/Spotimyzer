package it.hack.sasninjalabs.spotimyzer.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Aqeel Hashim on 23-Mar-18.
 */

public class User implements Serializable{

    private String UUID;

    private String name;

    private String phoneNumber;

    private String photoUrl;

    @Exclude
    private transient ArrayList<Spot> spots;

    public User() {

    }

    public User(String UUID, String name, String phoneNumber, String photoUrl) {
        this.UUID = UUID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoUrl = photoUrl;
        this.spots = new ArrayList<>();
    }
    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<Spot> getSpots() {
        return spots;
    }

    public void setSpots(ArrayList<Spot> spots) {
        this.spots = spots;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
