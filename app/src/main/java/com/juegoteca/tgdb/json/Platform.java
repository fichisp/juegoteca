package com.juegoteca.tgdb.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Platform {

    @SerializedName("data")
    @Expose
    private Map<String, PlatfotmObject> data;

    public Map<String, PlatfotmObject> getData() {
        return data;
    }

    public void setData(Map<String, PlatfotmObject> data) {
        this.data = data;
    }
}