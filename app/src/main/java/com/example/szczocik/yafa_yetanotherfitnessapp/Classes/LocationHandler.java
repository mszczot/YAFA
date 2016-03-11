package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.szczocik.yafa_yetanotherfitnessapp.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by szczocik on 11/03/16.
 */
public class LocationHandler implements Serializable {

    private RunningSession rs;
    private LocationObject lo;
    private DatabaseHandler db;
    private MainActivity mainActivity;
    private ArrayList<LocationObject> locObjList;
    private ArrayList<RunningSession> rsList;

    private int seqOrder;

    /**
     * Constructor
     */
    public LocationHandler(DatabaseHandler db, MainActivity ma) {
        this.db = db;
        this.mainActivity = ma;
        locObjList = db.getLocations();
        rsList = db.getSessions();

    }

    /**
     * public methods
     */
    public void startSession(){
        rs = new RunningSession();
        rs.setSessionId(db.addSession(rs));
        seqOrder = 0;
        test(rs);
    }

    public void stopSession() {
        rs.stop();
        db.updateSession(rs);
        db.addLocationsFromList(rs.getLocObjList());
        updateList();
        rs = null;
        seqOrder = 0;
    }

    public void addLocationToSession(Location l) {
        if (rs != null) {
            Toast.makeText(mainActivity, "Location added", Toast.LENGTH_LONG).show();
            lo = new LocationObject(l, seqOrder);
            lo.setSessionId(rs.getSessionId());
            rs.addLocation(lo);
            seqOrder++;
        }
    }

    public void addLocationToSession(LocationObject l) {

        if (rs != null) {
            Toast.makeText(mainActivity, "Location added", Toast.LENGTH_LONG).show();
            lo = new LocationObject(l.getLatitude(),l.getLongitude(),rs.getSessionId(),seqOrder);
            lo.setSessionId(rs.getSessionId());

            rs.addLocation(lo);
            seqOrder++;
        }
    }

    public float getCurrentSpeed() {

        return rs.calculateCurrentSpeed();
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

        private void test(RunningSession rs){
        LocationObject loc;
        loc = new LocationObject();
        loc.setLongitude((float) -3.166752);
        loc.setLatitude((float) 55.975193);
        addLocationToSession(loc);

        loc = new LocationObject();
        loc.setLongitude((float)-3.1846969);
        loc.setLatitude((float) 55.9600775);
            addLocationToSession(loc);

//        loc = new LocationObject();
//        loc.setLongitude((float) -3.159359);
//        loc.setLatitude((float) 55.962507);
//            addLocationToSession(loc);
//
//
//        loc = new LocationObject();
//        loc.setLongitude((float) -3.158865);
//        loc.setLatitude((float) 55.961958);
//            addLocationToSession(loc);
//
//        loc = new LocationObject();
//        loc.setLongitude((float) -3.158126);
//        loc.setLatitude((float) 55.960627);
//            addLocationToSession(loc);
    }

}
