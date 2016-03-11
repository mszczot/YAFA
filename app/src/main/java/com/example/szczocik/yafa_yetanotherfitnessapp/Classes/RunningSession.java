package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.RunningFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by szczocik on 09/03/16.
 */
public class RunningSession {

    /**
     * Variables
     */
    private long startTime;
    private long endTime;
    private ArrayList<LocationObject> locObjList;
    private float currentSpeed;
    private float avgSpeed;
    private ArrayList<Float> speed = new ArrayList<>();
    private float distance;
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
        this.locObjList = new ArrayList<>();
    }

    public RunningSession() {
        this.startTime = Calendar.getInstance().getTimeInMillis();
        this.locObjList = new ArrayList<>();
    }

    /**
     * public Methods
     */
    public void stop() {
        this.endTime = Calendar.getInstance().getTimeInMillis();
    }

    public void addLocation(LocationObject l) {
        l.setSessionId(this.sessionId);
        locObjList.add(l.getSeqOrder(), l);
        if (locObjList.size() >= 2) {
            this.distance = (float) l.distHaversine(locObjList.get(l.getSeqOrder() - 1));
        }
    }

    public float calculateCurrentSpeed() {
        currentSpeed = 0;
        if (locObjList.size() > 2) {
            LocationObject l1 = locObjList.get(locObjList.size()-1);
            LocationObject l2 = locObjList.get(locObjList.size());
            //TODO calculate current speed
        }
        return currentSpeed;
    }

    public float getAverageSpeed() {
        float sum = 0;
        if (speed != null) {
            for (int i=0;i<speed.size();i++) {
                sum += speed.get(i);
            }
        }
        return sum/speed.size();
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

    public void calculateDistance() {
        if (locObjList.size() >= 2) {
            for (int i = 0; i < locObjList.size() - 1; i++) {
                this.distance += locObjList.get(i).distanceTo(locObjList.get(i + 1));
            }
        }
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

    public ArrayList<LocationObject> getLocObjList() {
        return locObjList;
    }

    public void setLocObjList(ArrayList<LocationObject> locObjList) {
        this.locObjList = locObjList;
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
}