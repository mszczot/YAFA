package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;

/**
 * Created by szczocik on 10/03/16.
 */
class LocationObject {
    /**
     * Variables
     */
    private float latitude;
    private float longitude;
    private int sessionId;
    private int seqOrder;

    /**
     * Constructors
     */
    public LocationObject() {
    }

    public LocationObject(Location l, int seqOrder) {
        this.latitude = (float)l.getLatitude();
        this.longitude = (float)l.getLongitude();
        this.seqOrder = seqOrder;
    }

    public LocationObject(float latitude, float longitude, int sessionId, int seqOrder) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.sessionId = sessionId;
        this.seqOrder = seqOrder;
    }

    /**
     * Methods
     */
    public float distanceTo(LocationObject l) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(l.getLatitude()-this.latitude);
        double dLng = Math.toRadians(l.getLongitude()-this.longitude);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(l.getLatitude())) * Math.cos(Math.toRadians(this.latitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    /**
     * Method taken from: https://github.com/Mallfurion/hiker-android-app/blob/master/src/ro/scoutarad/hiker/Hiker.java
     */
    public double distHaversine(LocationObject l) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(l.getLatitude()-this.latitude);
        double dLng = Math.toRadians(l.getLongitude()-this.longitude);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(l.getLatitude()));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    /**
     * Getters and setters
     */
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getSeqOrder() {
        return seqOrder;
    }

    public String toString() {
        return "Lat: " + String.valueOf(this.latitude) + " Long: " + String.valueOf(this.longitude) + " Session id: " + String.valueOf(this.sessionId);
    }
}
