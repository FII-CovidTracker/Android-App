package com.fii.covidtracker.network.apis.regions;

import androidx.lifecycle.LiveData;

import com.fii.covidtracker.network.apis.ApiResponse;
import com.fii.covidtracker.network.responses.regions.RegionResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CovidTrackerRegionsApi {

    @GET("region/{regionId}")
    LiveData<ApiResponse<RegionResponse>> getRegion(@Path("regionId") int regionId);

    @GET("region")
    LiveData<ApiResponse<List<RegionResponse>>> getRegions();

}
