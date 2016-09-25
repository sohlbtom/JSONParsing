package com.example.android.jsonparsing;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by tommi on 24.9.2016.
 */
public class DBHandler extends SQLiteOpenHelper implements StationListener {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "StationDB.db";
    private static final String TABLE_NAME = "station_table";
    private static final String KEY_ID = "_id";
    private static final String KEY_STATIONNAME = "_stationName";
    private static final String KEY_STATIONSHORTCODE = "_stationShortCode";
    private static final String KEY_STATIONUICCODE = "_stationUICCode";

    String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_STATIONNAME+" TEXT,"+KEY_STATIONSHORTCODE+" TEXT,"+KEY_STATIONUICCODE+" INTEGER)";
    String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void addStation(Station station) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_STATIONNAME, station.getStationName());
            values.put(KEY_STATIONSHORTCODE, station.getStationShortCode());
            values.put(KEY_STATIONUICCODE,station.getStationUICCode());
            db.insert(TABLE_NAME, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    @Override
    public ArrayList<Station> getAllStation() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Station> stationList = null;
        try{
            stationList = new ArrayList<Station>();
            String QUERY = "SELECT * FROM "+TABLE_NAME;
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Station station = new Station();
                    station.setId(cursor.getInt(0));
                    station.setStationName(cursor.getString(1));
                    station.setStationShortCode(cursor.getString(2));
                    station.setStationUICCode(cursor.getInt(3));
                    stationList.add(station);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return stationList;
    }

    @Override
    public int getStationCount() {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            String QUERY = "SELECT * FROM "+TABLE_NAME;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }

}
