package com.fii.covidtracker.viewmodels.region;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fii.covidtracker.network.Resource;
import com.fii.covidtracker.repositories.regions.RegionRepository;
import com.fii.covidtracker.repositories.models.regions.Region;
import com.fii.covidtracker.repositories.models.regions.Region;
import com.fii.covidtracker.repositories.regions.RegionRepository;

import java.util.List;

import javax.inject.Inject;

public class RegionViewModel extends ViewModel {
    private static final String TAG = "RegionViewModel";

    private RegionRepository regionRepository;

    private LiveData<Resource<Region>> regionResource = null;
    private LiveData<Resource<List<Region>>> regionsResource = null;
    private int regionId;

    @Inject
    RegionViewModel(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
        this.regionResource = regionRepository.getRegion(this.regionId);
    }

    public LiveData<Resource<Region>> getRegionResource() {
        if (regionResource == null){
            this.regionResource = regionRepository.getRegion(this.regionId);
        }
        return regionResource;
    }

    public LiveData<Resource<List<Region>>> getRegionsResource(boolean forceFetch) {
        if (regionsResource == null){
            this.regionsResource = regionRepository.getRegions(forceFetch);
        }
        return regionsResource;
    }
}