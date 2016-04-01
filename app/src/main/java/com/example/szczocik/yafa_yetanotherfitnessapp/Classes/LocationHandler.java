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
 * Created by Marcin Szczot (40180425) on 11/03/16.
 * Class that interacts with RunningSession and DatabaseHandler
 * Singleton
 */
public class LocationHandler implements Parcelable {

    //region variables
    private static LocationHandler mInstance = null;

    private RunningSession rs;
    private DatabaseHandler db;
    private ArrayList<RunningSession> rsList;
    //endregion

    //region constructors
    private LocationHandler(DatabaseHandler db) {
        this.db = db;
        rsList = db.getSessions();
    }

    public static LocationHandler getInstance(DatabaseHandler db) {
        if (mInstance == null) {
            mInstance = new LocationHandler(db);
        }
        return mInstance;
    }
    //endregion

    //region public methods

    /**
     * Method to start the session
     */
    public void startSession(){
        rs = new RunningSession();
        rs.setSessionId(db.addSession(rs));
    }

    /**
     * Method to stop the session
     */
    public void stopSession() {
        rs.stop();
    }

    /**
     * Method to save the current session in the database and update the history list
     * @param ma
     */
    public void saveCurrentSession(MainActivity ma) {
        db.updateSession(rs);
        db.addLocationsFromList(rs.getLocList(), rs.getSessionId());
        updateList(ma);
        rs = null;
    }

    /**
     * Method to discard the current session
     */
    public void discardCurrentSession() {
        rs = null;
    }

    /**
     * Method to add the location to current session
     * @param l
     */
    public void addLocationToSession(Location l) {
        if (rs != null) {
            rs.addLocation(l);
        }
    }

    /**
     * Method to retrieve the Pace
     * @return
     */
    public String getPace(){
        if (rs != null) {
            return rs.getPaceAsString();
        } else {
            return "00:00";
        }
    }

    /**
     * Method to retreve the average speed
     * @return
     */
    public double getCurrentAvgSpeed() {
        if (rs != null) {
            return rs.getAvgSpeed();
        }
        return 0.00;
    }

    /**
     * Method to check if running session exists
     * @return
     */
    public boolean isInSession(){
        if (rs != null) {
            return true;
        }
        return false;
    }

    /**
     * Method to get the current session
     * returns RunningSession object
     * @return
     */
    public RunningSession getCurrentSession() {
        return this.rs;
    }

    /**
     * Method to retrieve the RunningSession from the list
     * takes int position as parameter
     * @param position
     * @return
     */
    public RunningSession getRSFromList(int position) {
        return rsList.get(position);
    }

    /**
     * Method that returns all RunningSessions
     * @return
     */
    public ArrayList<RunningSession> getRsList() {
        return this.rsList;
    }

    /**
     * Method that retrieves the RunningSession for given month
     * @param compareDate
     * @return
     */
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
    //endregion

    //region private methods

    /**
     * Method to update the list in History fragment
     * @param ma
     */
    private void updateList(MainActivity ma) {
        ma.updateList(this);
    }
    //endregion

    //region parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(rs, flags);
        dest.writeTypedList(rsList);
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
    //endregion
}
