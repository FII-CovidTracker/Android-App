package com.fii.covidtracker.helpers.mappers;

import android.util.Log;

import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;
import com.fii.covidtracker.local_db.entities.regions.RegionEntity;
import com.fii.covidtracker.network.responses.articles.ArticleResponse;
import com.fii.covidtracker.network.responses.regions.RegionResponse;
import com.fii.covidtracker.repositories.models.articles.Article;
import com.fii.covidtracker.repositories.models.regions.Region;

import java.util.List;
import java.util.stream.Collectors;

public class RegionMapper {
    private static final String TAG = "RegionMapper";

    public static RegionEntity map(RegionResponse regionResponse) {
        return new RegionEntity(
                regionResponse.id,
                regionResponse.name,
                regionResponse.global,
                0,
                0
        );
    }

    public static Region map(RegionEntity regionEntity) {
        Log.d(TAG, "map: "+ regionEntity.id);
        return new Region(
                regionEntity.id,
                regionEntity.name,
                regionEntity.global,
                regionEntity.createdAt,
                regionEntity.modifiedAt
        );
    }

    public static List<Region> map(List<RegionEntity> regionEntities) {
        return regionEntities
                .stream()
                .map(RegionMapper::map)
                .collect(Collectors.toList());
    }
}
