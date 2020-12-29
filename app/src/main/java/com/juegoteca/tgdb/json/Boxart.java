package com.juegoteca.tgdb.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Boxart {

    @SerializedName("base_url")
    @Expose
    private BaseUrl baseUrl;
    @SerializedName("data")
    @Expose
    private Map<String, BoxartObject[]> data;

    public BaseUrl getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(BaseUrl baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, BoxartObject[]> getData() {
        return data;
    }

    public void setData(Map<String, BoxartObject[]> data) {
        this.data = data;
    }
}