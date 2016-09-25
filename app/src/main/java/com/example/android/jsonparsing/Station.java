package com.example.android.jsonparsing;

/**
 * Created by tommi on 24.9.2016.
 */
public class Station {
    private int id;
    private String stationName;
    private String stationShortCode;
    private Integer stationUICCode;

    public Station() {

    }


    public Station(String stationName,String stationShortCode, Integer stationUICCode){
        this.stationName = stationName;
        this.stationShortCode = stationShortCode;
        this.stationUICCode = stationUICCode;
    }

    public Station(int id, String stationName, String stationShortCode, Integer stationUICCode){
        this.id = id;
        this.stationName = stationName;
        this.stationShortCode = stationShortCode;
        this.stationUICCode = stationUICCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationShortCode() {
        return stationShortCode;
    }

    public void setStationShortCode(String stationShortCode) {
        this.stationShortCode = stationShortCode;
    }

    public Integer getStationUICCode() {
        return stationUICCode;
    }

    public void setStationUICCode(Integer stationUICCode) {
        this.stationUICCode = stationUICCode;
    }
}
