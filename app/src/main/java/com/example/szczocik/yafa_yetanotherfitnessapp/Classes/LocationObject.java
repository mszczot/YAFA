package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

/**
 * Created by szczocik on 10/03/16.
 */
public class LocationObject {
    private float latitude;
    private float longitude;
    private int sessionId;

    public LocationObject() {
    }

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

    public String toString() {
        return "Lat: " + String.valueOf(this.latitude) + " Long: " + String.valueOf(this.longitude) + " Session id: " + String.valueOf(this.sessionId);
    }
}
