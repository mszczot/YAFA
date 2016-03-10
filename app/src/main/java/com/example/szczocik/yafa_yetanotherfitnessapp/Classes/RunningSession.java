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

    private long startTime;
    private long endTime;
    private ArrayList<Location> locationList;
    private float currentSpeed;
    private float avgSpeed;
    private ArrayList<Float> speed = new ArrayList<>();
    private float distance;
    private int sessionId;

    private String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    String[] months = {"January","February","March","April","May","June",
            "July","August","September","October","November","December"};

    public RunningSession(long st, long et, float dist) {
        this.startTime = st;
        this.endTime = et;
        this.distance = dist;
        locationList = new ArrayList<>();
    }

    public RunningSession(Location sl){
        this.startTime = Calendar.getInstance().getTimeInMillis();
        locationList = new ArrayList<>();
        if (sl != null) {
            locationList.add(sl);
        }
        printTime(this.startTime);
    }

    public void stop() {
        this.endTime = Calendar.getInstance().getTimeInMillis();
        printTime(this.endTime);
    }

    public void printTime(long t){
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(t);
        String time = "" + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE);
        Log.d("Click", time);
    }


    public void addLocation(Location l) {
        locationList.add(l);
        Log.d("Location", locationList.toString());
        speed.add(l.getSpeed());
    }

    public float calculateCurrentSpeed() {
        currentSpeed = 0;
        if (locationList.size() > 2) {
            Location l1 = locationList.get(locationList.size()-1);
            Location l2 = locationList.get(locationList.size());

            float dist = l1.distanceTo(locationList.get(locationList.size()));
            currentSpeed = dist/(l1.getTime() - l2.getTime());
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

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getStartDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);
        String startDate = days[cl.get(Calendar.DAY_OF_WEEK)] + ", " + cl.get(Calendar.DATE)
                + getDayEnd(cl.get(Calendar.DATE)) + " of " + months[cl.get(Calendar.MONTH)];
        return startDate;
    }

    public float getAvgSpeed() {
        return avgSpeed;
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
        if (locationList != null) {
            distance = locationList.get(0).distanceTo(locationList.get(locationList.size()-1));
        }
    }

    public float getDistance() {
        return this.distance;
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

    public String locationListToString() {
        String list = "";

        for(Location l:locationList) {
            list += " " + l.toString() + "\n";
        }

        return list;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}