package com.example.administrator.ourlovehut;

import org.litepal.crud.LitePalSupport;

public class Datas extends LitePalSupport {
    private Double TempData;
    private String TimeData;
    private Double LightData;
    private int id;

    public Double getTempData() {
        return TempData;
    }

    public void setTempData(Double tempData) {
        TempData = tempData;
    }

    public String getTimeData() {
        return TimeData;
    }

    public void setTimeData(String timeData) {
        TimeData = timeData;
    }

    public Double getLightData() {
        return LightData;
    }

    public void setLightData(Double lightData) {
        LightData = lightData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
