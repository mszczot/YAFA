package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by szczocik on 09/03/16.
 */
public class RunningSession implements Parcelable{

//region static variables
    private static double KM_TO_MILES = 0.621371;
    private static String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday"};
    private static String[] months = {"January","February","March","April","May","June",
            "July","August","September","October","November","December"};
//endregion

//region variables
    private long startTime;
    private long endTime;
    private ArrayList<Location> locList;
    private float avgSpeed; //miles per hour
    private float speed;
    private float distance = 0; //in meters
    private int sessionId;
    private float pace; //minutes per mile
    private long duration;
    private float maxSpeed = 0;
    private float elevationGain;
    private float elevationLoss;

    private double currentAltitude;
//endregion

//region constructors
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


//endregion

//region public methods

    protected RunningSession(Parcel in) {
        startTime = in.readLong();
        endTime = in.readLong();
        locList = in.createTypedArrayList(Location.CREATOR);
        avgSpeed = in.readFloat();
        speed = in.readFloat();
        distance = in.readFloat();
        sessionId = in.readInt();
        pace = in.readFloat();
        duration = in.readLong();
        maxSpeed = in.readFloat();
        elevationGain = in.readFloat();
        elevationLoss = in.readFloat();
        currentAltitude = in.readDouble();
    }

    public static final Creator<RunningSession> CREATOR = new Creator<RunningSession>() {
        @Override
        public RunningSession createFromParcel(Parcel in) {
            return new RunningSession(in);
        }

        @Override
        public RunningSession[] newArray(int size) {
            return new RunningSession[size];
        }
    };

    /**
     * Method to stop the session
     */
    public void stop() {
        this.endTime = Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Method to add location to running session
     * @param l - Location to be added
     */
    public void addLocation(Location l) {
        if (locList.size() > 1) {
            if (l.distanceTo(locList.get(locList.size() - 1)) >= 15) {

                if (l.hasSpeed()) {
                    this.speed = l.getSpeed();
                }

                this.distance += l.distanceTo(locList.get(locList.size() - 1));

                locList.add(l);
                calculateAvgSpeed();
                setMaxSpeed(l);
                setElevation(l);

                if (l.hasAltitude()) {
                    this.currentAltitude = (float) l.getAltitude();
                    setElevation(l);
                }
            }
        } else {
            locList.add(l);
        }
    }

    /**
     * Method to get formatted start date
     * @return String date in format: Monday, 2nd of June
     */
    public String getStartDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);
        String startDate = days[cl.get(Calendar.DAY_OF_WEEK) - 1] + ", " + cl.get(Calendar.DATE)
                + getDayEnd(cl.get(Calendar.DATE)) + " of " + months[cl.get(Calendar.MONTH)];
        return startDate;
    }

    /**
     * Method to get total time in string format
      * @return string format of total time in format: HH:MM:SS
     */
    public String getTotalTime() {
        long totalsec = (endTime - startTime)/1000;
        return formatTime(totalsec);
    }
//endregion

//region private methods
    private String formatTime(long time) {
        NumberFormat f = new DecimalFormat("00");
        long minutes = time/60;
        long hours = minutes/60;
        long secs = time%60;

        if (minutes == 60) {
            minutes = 0;
        }

        return f.format(hours) + ":" + f.format(minutes) + ":" + f.format(secs);
    }

    private void setMaxSpeed(Location l) {
        float speedCalc = calculateSpeedBetween2Locations(l);
        if (this.maxSpeed != 0) {
            if (l.hasSpeed() && l.getSpeed() > this.maxSpeed) {
                this.maxSpeed = l.getSpeed();
            } else if (speedCalc > this.maxSpeed) {
                this.maxSpeed = speedCalc;
            }
        } else {
            if (l.hasSpeed()) {
                this.maxSpeed = l.getSpeed();
            } else {
                this.maxSpeed = speedCalc;
            }
        }
    }

    private float calculateSpeedBetween2Locations(Location currLocation) {
        float dist = locList.get(locList.size() - 1).distanceTo(currLocation); //distance in meters
        // duration between points in long format
        long duration = currLocation.getTime() - locList.get(locList.size() - 1).getTime();
        return dist/duration * 3600;
    }

    private void setElevation(Location l) {
        if (this.currentAltitude != 0) {
            if (l.getAltitude()-this.currentAltitude > 0) {
                this.elevationGain += l.getAltitude() - this.currentAltitude;
            } else {
                this.elevationLoss += l.getAltitude() - this.currentAltitude;
            }
        } else {
            this.currentAltitude = l.getAltitude();
        }
    }

    private void calculateAvgSpeed(){
        Calendar cl = Calendar.getInstance();
        this.avgSpeed = getDistanceInMiles()/(cl.getTimeInMillis()-this.startTime) * 3600000; //miles per hour
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

    private void calculatePace() {
        //minutes per mile
        setDuration();
        if (this.endTime != 0) {
            this.pace = (this.endTime - this.startTime)/getDistanceInMiles() * 0.00001667f;
        } else {
            float distinmiles = getDistanceInMiles();
            this.pace = this.duration/distinmiles * 0.00001667f;
        }
    }

    private void setDuration() {
        if (locList.size() >= 2) {
            this.duration = locList.get(locList.size()-1).getTime() - this.startTime;
        }
    }
//endregion

//region accessors
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public float getDistance() {
        return this.distance;
    }

    public float getDistanceInKm() {return this.distance/1000;}

    public float getDistanceInMiles() {
        return this.getDistanceInKm() * (float)KM_TO_MILES;
    }

    public float getAvgSpeed() {
        calculateAvgSpeed();
        return avgSpeed;
    }

    public float getAvgSpeedValue() {
        return avgSpeed;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        return sdf.format(new Date(this.startTime));
    }

    public long getLongStartTime() {
        return this.startTime;
    }

    public String getDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        return sdf.format(new Date(this.startTime));
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

    public float getPace() {
        calculatePace();
//        if (this.pace > 60) {
//            return 0;
//        }
        return pace;
    }

    public float getPaceValue() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public String getPaceAsString() {
        float f = getPace();
        int p = (int)f;

        int seconds = (int) (60 * (this.pace - (float) p));
        if (p > 60) {
            return "--:--";
        } else {
            return "" + String.valueOf(p) + ":" + String.format("%02d", seconds);
        }
    }

    public float getMaxSpeed() {

        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getElevationLoss() {
        return elevationLoss;
    }

    public void setElevationLoss(float elevationLoss) {
        this.elevationLoss = elevationLoss;
    }

    public float getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(float elevationGain) {
        this.elevationGain = elevationGain;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeTypedList(locList);
        dest.writeFloat(avgSpeed);
        dest.writeFloat(speed);
        dest.writeFloat(distance);
        dest.writeInt(sessionId);
        dest.writeFloat(pace);
        dest.writeLong(duration);
        dest.writeFloat(maxSpeed);
        dest.writeFloat(elevationGain);
        dest.writeFloat(elevationLoss);
        dest.writeDouble(currentAltitude);
    }

    //endregion

//region Parcelable methods

//endregion

}