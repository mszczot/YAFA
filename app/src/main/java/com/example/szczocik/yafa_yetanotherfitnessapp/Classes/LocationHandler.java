package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by szczocik on 11/03/16.
 */
public class LocationHandler implements Serializable, Parcelable {

    private static LocationHandler mInstance = null;

    private RunningSession rs;
    private DatabaseHandler db;
    private MainActivity mainActivity;
    private ArrayList<RunningSession> rsList;


    /**
     * Constructor
     */
    private LocationHandler(DatabaseHandler db, MainActivity ma) {
        this.db = db;
        this.mainActivity = ma;
        rsList = db.getSessions();
    }

    public static LocationHandler getInstance(DatabaseHandler db, MainActivity ma) {
        if (mInstance == null) {
            mInstance = new LocationHandler(db, ma);
        }
        return mInstance;
    }

    protected LocationHandler(Parcel in) {
        rs = in.readParcelable(RunningSession.class.getClassLoader());
        rsList = in.createTypedArrayList(RunningSession.CREATOR);
    }

    public static final Creator<LocationHandler> CREATOR = new Creator<LocationHandler>() {
        @Override
        public LocationHandler createFromParcel(Parcel in) {
            return new LocationHandler(in);
        }

        @Override
        public LocationHandler[] newArray(int size) {
            return new LocationHandler[size];
        }
    };

    /**
     * public methods
     */
    public void startSession(){
        rs = new RunningSession();
        rs.setSessionId(db.addSession(rs));
    }

    public void stopSession() {
        rs.stop();
    }

    public void saveCurrentSession() {
        db.updateSession(rs);
        db.addLocationsFromList(rs.getLocList(), rs.getSessionId());
        updateList();
        rs = null;
    }

    public void disardCurrentSession() {
        rs = null;
    }

    public void addLocationToSession(Location l) {
        if (rs != null) {
            rs.addLocation(l);
        }
    }


    public double getCurrentSpeed() {
        if (rs != null) {
            return rs.getSpeed();
        }
        return 0.00;
    }

    public String getPace(){
        if (rs != null) {
            return rs.getPaceAsString();
        } else {
            return "00:00";
        }
    }

    public double getCurrentAvgSpeed() {
        if (rs != null) {
            return rs.getAvgSpeed();
        }
        return 0.00;
    }

    public boolean isInSession(){
        if (rs != null) {
            return true;
        }
        return false;
    }

    public String getDuration() {
        if (rs != null) {
            return rs.getTotalTime();
        } else {
            return "";
        }
    }

    public RunningSession getCurrentSession() {
        return this.rs;
    }

    public RunningSession getRSFromList(int position) {
        return rsList.get(position);
    }

    public int getRSListSize() {
        return rsList.size();
    }

    public ArrayList<RunningSession> getRsList() {
        return this.rsList;
    }

    public Location getPreviousLocation() {
        return rs.getLocList().get(rs.getLocList().size() - 2);
    }

    public ArrayList<RunningSession> getSessionForMonth(Calendar compareDate) {
        ArrayList<RunningSession> list = new ArrayList<>();

        for (RunningSession rs:rsList) {
            Calendar rsDate = Calendar.getInstance();
            rsDate.setTimeInMillis(rs.getLongStartTime());
            if (rsDate.get(Calendar.MONTH) == compareDate.get(Calendar.MONTH)) {
                list.add(rs);
            }
        }
        return list;
    }

    /**
     * Private methods
     */
    private void updateList() {
        mainActivity.updateList(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(rs, flags);
        dest.writeTypedList(rsList);
    }


}
