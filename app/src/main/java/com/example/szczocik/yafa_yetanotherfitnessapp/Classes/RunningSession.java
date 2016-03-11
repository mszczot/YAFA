package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by szczocik on 09/03/16.
 */
public class RunningSession implements Serializable {

    /**
     * Variables
     */
    private long startTime;
    private long endTime;
    private ArrayList<Location> locList;
    private float avgSpeed;
    private float speed;
    private float distance = 0;
    private int sessionId;

    private String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    String[] months = {"January","February","March","April","May","June",
            "July","August","September","October","November","December"};


    /**
     * Constructors
     */
    public RunningSession(long st, long et, float dist, int sId) {
        this.startTime = st;
        this.endTime = et;
        this.distance = dist;
        this.sessionId = sId;
        this.locList = new ArrayList<>();
    }

    public RunningSession() {
        this.startTime = Calendar.getInstance().getTimeInMillis();
        this.locList = new ArrayList<>();
    }

    /**
     * public Methods
     */
    public void stop() {
        this.endTime = Calendar.getInstance().getTimeInMillis();
    }

    public void addLocation(Location l) {
        locList.add(l);
        if (l.hasSpeed()) {
            this.speed = l.getSpeed();
        }
        if (locList.size() >= 2) {
            this.distance += l.distanceTo(locList.get(locList.size()-2));
        }
        calculateAvgSpeed();
    }

    public String getStartDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);
        String startDate = days[cl.get(Calendar.DAY_OF_WEEK)] + ", " + cl.get(Calendar.DATE)
                + getDayEnd(cl.get(Calendar.DATE)) + " of " + months[cl.get(Calendar.MONTH)];
        return startDate;
    }

    public String getTotalTime() {
        NumberFormat f = new DecimalFormat("00");

        long totalsec = (endTime - startTime)/1000;
        long minutes = totalsec/60;
        long hours = minutes/60;
        long secs = totalsec%60;

        String totalTime = f.format(hours) + ":" + f.format(minutes) + ":" + f.format(secs);

        return totalTime;
    }

    private void calculateAvgSpeed(){
        Calendar cl = Calendar.getInstance();
        this.avgSpeed = this.distance/(cl.getTimeInMillis()-this.startTime);
    }

    private String getDayEnd(int day) {
        switch(day) {
            case 1:
                return "<sup><small>st</small></sup>";
            case 2:
                return "<sup><small>nd</small></sup>";
            case 3:
                return "<sup><small>rd</small></sup>";
            default:
                return "<sup><small>th</small></sup>";
        }
    }

    /**
     * Setters and getters
     */
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public float getDistance() {
        return this.distance;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public ArrayList<Location> getLocList() {
        return locList;
    }

    public void setLocList(ArrayList<Location> locList) {
        this.locList = locList;
    }

    public float getSpeed() {
        return speed;
    }
}