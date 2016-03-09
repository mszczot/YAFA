package com.example.szczocik.yafa_yetanotherfitnessapp.HelperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.example.szczocik.yafa_yetanotherfitnessapp.Classes.RunningSession;

import java.io.Serializable;

/**
 * Created by szczocik on 09/03/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper implements Serializable {

    //All static variables
    private static final int DATABASE_VERSION = 1;
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


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + START_TIME + " INTEGER,"
                + END_TIME + " INTEGER," + AVG_SPEED + " REAL" + ")";
        db.execSQL(CREATE_TABLE_SESSIONS);
        String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + LOCATION_ID + " INTEGER PRIMARY KEY," + LOCATION_LAT + " REAL,"
                + LOCATION_LONG + " REAL" + ")";
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        // Create tables again
        onCreate(db);
    }

    public void addSession(RunningSession rs) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(START_TIME, rs.getStartTime());
        values.put(END_TIME, rs.getEndTime());
        values.put(AVG_SPEED, rs.getAvgSpeed());

        db.insert(TABLE_SESSIONS, null, values);
        db.close();
    }

    public void addLocation(Location loc, long st) {
        String getSessionId = "SELECT " + KEY_ID + " FROM " + TABLE_SESSIONS +
                " WHERE " + START_TIME + " = " + st;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(getSessionId, null);
        int sessionId = cursor.getInt(1);
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(LOCATION_LAT, loc.getLatitude());
        values.put(LOCATION_LONG, loc.getLongitude());
        values.put(SESSION_ID, sessionId);
        db.insert(TABLE_LOCATIONS, null, values);
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
}