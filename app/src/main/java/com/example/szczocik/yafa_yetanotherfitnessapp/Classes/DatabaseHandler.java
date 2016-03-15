package com.example.szczocik.yafa_yetanotherfitnessapp.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by szczocik on 09/03/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper implements Serializable {

    //All static variables
    private static final int DATABASE_VERSION = 20;
    //database name
    private static final String DATABASE_NAME = "runningManager";
    /**
     *Table for storing sessions
     */
    //session table
    private static final String TABLE_SESSIONS = "sessions";
    //Sessions table columns names
    private static final String KEY_ID = "id";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String AVG_SPEED = "avg_speed";
    private static final String DISTANCE = "dist";
    private static final String MAX_SPEED = "max_speed";
    private static final String ELEVATION_GAIN = "elevation_gain";
    private static final String ELEVATION_LOSS = "elevation_loss";
    private static final String PACE = "pace";

    /**
     * Table for storing locations
     */
    //locations table
    private static final String TABLE_LOCATIONS = "locations";
    //locations table columns names
    private static final String LOCATION_ID = "loc_id";
    private static final String LOCATION_LONG = "loc_long";
    private static final String LOCATION_LAT = "loc_lat";
    private static final String SESSION_ID = "session_id";
    private static final String ALTITUDE = "altitude";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + START_TIME + " INTEGER,"
                + END_TIME + " INTEGER," + AVG_SPEED + " REAL, " + DISTANCE + " REAL, "
                + MAX_SPEED + " REAL, " + ELEVATION_GAIN + " REAL, " + ELEVATION_LOSS
                + " REAL, " + PACE + " REAL)";
        db.execSQL(CREATE_TABLE_SESSIONS);
        String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + LOCATION_ID + " INTEGER PRIMARY KEY," + LOCATION_LAT + " REAL,"
                + LOCATION_LONG + " REAL, " + SESSION_ID + " INTEGER " + ALTITUDE
                + " REAL)";
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        // Create tables again
        onCreate(db);
    }

    public int addSession(RunningSession rs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(START_TIME, rs.getLongStartTime());
        values.put(END_TIME, rs.getEndTime());
        values.put(AVG_SPEED, rs.getAvgSpeed());
        values.put(DISTANCE, rs.getDistance());
        values.put(MAX_SPEED, rs.getMaxSpeed());

        db.insert(TABLE_SESSIONS, null, values);

        int sessionId;

        String getSessionId = "SELECT " + KEY_ID + " FROM " + TABLE_SESSIONS +
                " WHERE " + START_TIME + " = " + rs.getLongStartTime();

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getSessionId, null);
        cursor.moveToFirst();
        sessionId = cursor.getInt(cursor.getColumnIndex(KEY_ID));
        cursor.close();
        db.close();

        return sessionId;
    }

    public void updateSession(RunningSession rs) {
        String condition = KEY_ID + " = " + rs.getSessionId();


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put(END_TIME, rs.getEndTime());
        newValues.put(AVG_SPEED, rs.getAvgSpeed());
        newValues.put(DISTANCE, rs.getDistance());
        newValues.put(MAX_SPEED, rs.getMaxSpeed());
        newValues.put(ELEVATION_GAIN, rs.getElevationGain());
        newValues.put(ELEVATION_LOSS, rs.getElevationLoss());
        newValues.put(PACE, rs.getPace());

        db.update(TABLE_SESSIONS, newValues, condition, null);

        db.close();
    }

    public void addLocationsFromList(ArrayList<Location> locList, int rsId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (Location l:locList) {
            values.put(LOCATION_LONG,l.getLongitude());
            values.put(LOCATION_LAT, l.getLatitude());
            values.put(SESSION_ID, rsId);
            values.put(ALTITUDE, l.getAltitude());
            Log.d("Adding locations", values.toString());
            db.insert(TABLE_LOCATIONS, null, values);
        }
        db.close();
    }

    public int getSessionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SESSIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public ArrayList<RunningSession> getSessions() {
        String getSessions = "SELECT * FROM " + TABLE_SESSIONS + " ORDER BY " + KEY_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getSessions, null);
        cursor.moveToFirst();
        ArrayList<RunningSession> rsList = new ArrayList<>();
        RunningSession rs;

        while (!cursor.isAfterLast()) {
            rs = new RunningSession(cursor.getLong(cursor.getColumnIndex(START_TIME)),
                    cursor.getLong(cursor.getColumnIndex(END_TIME)),
                    cursor.getFloat(cursor.getColumnIndex(DISTANCE)),
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            rs.setLocList(getLocationsForSession(rs.getSessionId()));
            rs.setMaxSpeed(cursor.getFloat(cursor.getColumnIndex(MAX_SPEED)));
            rs.setElevationGain(cursor.getFloat(cursor.getColumnIndex(ELEVATION_GAIN)));
            rs.setElevationLoss(cursor.getFloat(cursor.getColumnIndex(ELEVATION_LOSS)));
            rs.setPace(cursor.getFloat(cursor.getColumnIndex(PACE)));
            rsList.add(rs);
            cursor.moveToNext();
        }
        cursor.close();

        return  rsList;
    }

    public ArrayList<Location> getLocations(){
        ArrayList<Location> locList = new ArrayList<>();

        String getLocations = "SELECT * FROM " + TABLE_LOCATIONS;
        Log.d("DBHelper", getLocations);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLocations, null);
        cursor.moveToFirst();
        Location l;

        while (!cursor.isAfterLast()) {
            l = new Location("");
            l.setLatitude(cursor.getFloat(cursor.getColumnIndex(LOCATION_LAT)));
            l.setLongitude(cursor.getFloat(cursor.getColumnIndex(LOCATION_LONG)));

            locList.add(l);
            cursor.moveToNext();
        }
        Log.d("all locations", locList.toString());

        return locList;
    }

    private ArrayList<Location> getLocationsForSession(int sessionId) {
        ArrayList<Location> locList = new ArrayList<>();
        Location loc;

        String getLocations = "SELECT * FROM " + TABLE_LOCATIONS + " WHERE " +
                SESSION_ID + " = " + sessionId;
        Log.d("DBHelper", getLocations);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(getLocations, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            loc = new Location("");
            loc.setLatitude(cursor.getFloat(cursor.getColumnIndex(LOCATION_LAT)));
            loc.setLongitude(cursor.getFloat(cursor.getColumnIndex(LOCATION_LONG)));
            loc.setAltitude(cursor.getDouble(cursor.getColumnIndex(ALTITUDE )));
            locList.add(loc);
            cursor.moveToNext();
        }
        Log.d("Locations", locList.toString());

        return locList;
    }
}