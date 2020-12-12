package com.fii.covidtracker.network.responses.regions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegionResponse {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("global")
    @Expose
    public boolean global;
}
