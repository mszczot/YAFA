package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;
import android.util.Log;

import com.example.szczocik.yafa_yetanotherfitnessapp.Fragments.RunningFragment;

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
    private long timeOfLastUpdate;
    private float avgSpeed;



    public RunningSession(Location sl){
        this.startTime = Calendar.getInstance().getTimeInMillis();
        locationList = new ArrayList<>();
        if (sl != null) {
            locationList.add(sl);
        }
        printTime(this.startTime);
        showLocation(locationList.toString());
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

    public void showLocation(String l) {
        Log.d("Location", l);
    }

    public void addLocation(Location l) {
        locationList.add(l);
        Log.d("Location", locationList.toString());
        timeOfLastUpdate = Calendar.getInstance().getTimeInMillis();
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

    public float calculateAverageSpeed() {


        return 0;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }
}