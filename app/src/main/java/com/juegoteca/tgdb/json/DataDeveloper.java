
package com.juegoteca.tgdb.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class DataDeveloper {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("developers")
    @Expose
    private Map<String, DeveloperObject> developers;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Map<String, DeveloperObject> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Map<String, DeveloperObject> developers) {
        this.developers = developers;
    }

}
