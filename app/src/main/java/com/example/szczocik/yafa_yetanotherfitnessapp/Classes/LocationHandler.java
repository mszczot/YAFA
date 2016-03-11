package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;

import com.example.szczocik.yafa_yetanotherfitnessapp.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by szczocik on 11/03/16.
 */
public class LocationHandler implements Serializable {

    private RunningSession rs;
    private DatabaseHandler db;
    private MainActivity mainActivity;
    private ArrayList<RunningSession> rsList;


    /**
     * Constructor
     */
    public LocationHandler(DatabaseHandler db, MainActivity ma) {
        this.db = db;
        this.mainActivity = ma;
        rsList = db.getSessions();

    }

    /**
     * public methods
     */
    public void startSession(){
        rs = new RunningSession();
        rs.setSessionId(db.addSession(rs));
       // test(rs);
    }

    public void stopSession() {
        rs.stop();
        db.updateSession(rs);
        db.addLocationsFromList(rs.getLocList(), rs.getSessionId());
        updateList();
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


    /**
     * Private methods
     */
    private void updateList() {
        mainActivity.updateList(this);
    }

}
