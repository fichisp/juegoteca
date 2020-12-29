package com.juegoteca.tgdb.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Include {

    @SerializedName("boxart")
    @Expose
    private Boxart boxart;
    @SerializedName("platform")
    @Expose
    private Platform platform;

    public Boxart getBoxart() {
        return boxart;
    }

    public void setBoxart(Boxart boxart) {
        this.boxart = boxart;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

}