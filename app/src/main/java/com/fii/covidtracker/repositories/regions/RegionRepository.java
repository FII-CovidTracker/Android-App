package com.fii.covidtracker.repositories.regions;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.fii.covidtracker.AppExecutors;
import com.fii.covidtracker.helpers.mappers.RegionMapper;
import com.fii.covidtracker.local_db.dao.regions.RegionDao;
import com.fii.covidtracker.local_db.entities.regions.RegionEntity;
import com.fii.covidtracker.network.NetworkBoundResource;
import com.fii.covidtracker.network.Resource;
import com.fii.covidtracker.network.apis.ApiResponse;
import com.fii.covidtracker.network.apis.regions.CovidTrackerRegionsApi;
import com.fii.covidtracker.network.responses.regions.RegionResponse;
import com.fii.covidtracker.repositories.Repository;
import com.fii.covidtracker.repositories.models.regions.Region;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class RegionRepository extends Repository {
    private static final String TAG = "RegionRepository";
    private CovidTrackerRegionsApi api;
    private RegionDao regionDao;

    @Inject
    public RegionRepository(CovidTrackerRegionsApi api,
                            RegionDao regionDao, AppExecutors executors) {
        this.api = api;
        this.executors = executors;
        this.regionDao = regionDao;
    }

    public LiveData<Resource<Region>> getRegion(int regionId) {
        return new NetworkBoundResource<Region, RegionResponse>(executors) {
            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Fetching the article with id " + regionId + " failed.");
            }

            @Override
            protected void saveCallResult(RegionResponse regionResponse) {
                if (regionResponse != null) {

                    RegionEntity regionEntity = RegionMapper.map(regionResponse);

                    regionDao.upsert(regionEntity);

                    Log.i(TAG, "Saved the region with id " + regionId + " into our local database.");
                }
            }

            @Override
            protected boolean shouldFetch(Region region) {
                boolean fetch = RegionRepository.this.shouldFetch(region);
                Log.i(TAG, "shouldFetch region " + (region != null ? region.id : "<<new region>>") + ": " + fetch);
                return fetch;
            }

            @Override
            protected LiveData<Region> loadFromDb() {
                return Transformations.map(regionDao.getById(regionId), RegionMapper::map);
            }

            @Override
            protected LiveData<ApiResponse<RegionResponse>> createCall() {
                LiveData<ApiResponse<RegionResponse>> call = api.getRegion(regionId);
                Log.i(TAG, "Called the api for info related to the region with id " + regionId);
                return call;
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Region>>> getRegions(boolean forceFetch) {

        return new NetworkBoundResource<List<Region>, List<RegionResponse>>(executors) {
            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Fetching articles failed.");
            }

            @Override
            protected void saveCallResult(List<RegionResponse> regionResponses) {
                if (regionResponses != null) {

                    List<RegionEntity> regionEntities = regionResponses
                            .stream()
                            .map(RegionMapper::map)
                            .collect(Collectors.toList());

                    regionDao.upsert(regionEntities);
                    // Todo: modify here
                    Log.i(TAG, "Saved the regions into our local database.");
                }
            }

            @Override
            protected boolean shouldFetch(List<Region> regions) {
                boolean fetch = RegionRepository.this.shouldFetch(regions);
                Log.i(TAG, "shouldFetch regions: " + fetch);
                return forceFetch || fetch;
            }

            @Override
            protected LiveData<List<Region>> loadFromDb() {
                return Transformations.map(regionDao.getAll(), RegionMapper::map);
            }

            @Override
            protected LiveData<ApiResponse<List<RegionResponse>>> createCall() {
                LiveData<ApiResponse<List<RegionResponse>>> call = api.getRegions();
                Log.i(TAG, "Called the api for regions with");
                return call;
            }
        }.asLiveData();
    }
}
