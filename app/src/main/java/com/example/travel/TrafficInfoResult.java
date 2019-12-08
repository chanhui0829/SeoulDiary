package com.example.travel;

import org.json.JSONException;
import org.json.JSONObject;

public class TrafficInfoResult {
    private int pathType;
    private String info;
    private double trafficDistance;
    private int totalWalk;
    private int totalTime;
    private int payment;
    private int subwayTransitCount;
    private int busTransitCount;
    private int totalStationCount;
    private String firstStartStation;
    private String lastEndStation;
    private double totalDistance;
    private int busStationCount;
    private int subwayStationCount;
    private String subPath;
    private String mapObj;

    public int getPathType() {
        return pathType;
    }

    public double getTrafficDistance() {
        return trafficDistance;
    }

    public int getTotalWalk() {
        return totalWalk;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getPayment() {
        return payment;
    }


    public String getSubPath() {
        return subPath;
    }

    public int getBusStationCount() {
        return busStationCount;
    }

    public void setBusStationCount(int busStationCount) {
        this.busStationCount = busStationCount;
    }

    public int getSubwayStationCount() {
        return subwayStationCount;
    }

    public void setSubwayStationCount(int subwayStationCount) {
        this.subwayStationCount = subwayStationCount;
    }

    public TrafficInfoResult(JSONObject jsonObject) throws JSONException {
        this.info = jsonObject.getString("info");

        JSONObject InfoObject = new JSONObject(info);
        this.trafficDistance = InfoObject.getDouble("trafficDistance");
        this.totalWalk = InfoObject.getInt("totalWalk");
        this.totalTime = InfoObject.getInt("totalTime");
        this.payment = InfoObject.getInt("payment");
        this.subwayTransitCount = InfoObject.getInt("subwayTransitCount");
        this.busTransitCount = InfoObject.getInt("busTransitCount");
        this.firstStartStation = InfoObject.getString("firstStartStation");
        this.lastEndStation = InfoObject.getString("lastEndStation");
        this.totalStationCount = InfoObject.getInt("totalStationCount");
        this.totalDistance = InfoObject.getDouble("totalDistance");
        this.busStationCount = InfoObject.getInt("busStationCount");
        this.subwayStationCount = InfoObject.getInt("subwayStationCount");
        this.subPath = jsonObject.getString("subPath");
        this.mapObj = InfoObject.getString("mapObj");
        this.pathType = jsonObject.getInt("pathType");

    }

    public String getMapObj() {
        return mapObj;
    }
}



