package it.hack.sasninjalabs.spotimyzer.model;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by Aqeel Hashim on 23-Mar-18.
 */

public class Spot {

    private long id;

    private double latitude;

    private double longitude;

    private String nickname;

    private double price;

    @Exclude
    private User owner;

    @Exclude
    private ArrayList<Slot> slots;

    public Spot() {
    }

    public Spot(long id, double latitude, double longitude, String nickname, double price) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nickname = nickname;
        this.price = price;
        this.slots = new ArrayList<>();
    }

    public Spot(long id, double latitude, double longitude, String nickname, double price, User owner, ArrayList<Slot> slots) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nickname = nickname;
        this.price = price;
        this.owner = owner;
        this.slots = slots;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ArrayList<Slot> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<Slot> slots) {
        this.slots = slots;
    }

}
