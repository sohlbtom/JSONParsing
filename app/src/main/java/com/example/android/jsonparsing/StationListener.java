package com.example.android.jsonparsing;
import java.util.ArrayList;
/**
 * Created by tommi on 24.9.2016.
 */
public interface StationListener {

    public void addStation(Station station);
    public ArrayList<Station> getAllStation();
    public int getStationCount();

}
