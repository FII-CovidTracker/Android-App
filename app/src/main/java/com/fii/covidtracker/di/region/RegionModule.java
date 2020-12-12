package com.fii.covidtracker.di.region;

import com.fii.covidtracker.AppExecutors;
import com.fii.covidtracker.di.article.ArticleScope;
import com.fii.covidtracker.local_db.CovidTrackerDatabase;
import com.fii.covidtracker.local_db.dao.articles.ArticleDao;
import com.fii.covidtracker.local_db.dao.regions.RegionDao;
import com.fii.covidtracker.network.apis.articles.CovidTrackerArticlesApi;
import com.fii.covidtracker.network.apis.regions.CovidTrackerRegionsApi;
import com.fii.covidtracker.repositories.articles.ArticleRepository;
import com.fii.covidtracker.repositories.regions.RegionRepository;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class RegionModule {
    @RegionScope
    @Provides
    static RegionDao providesRegionDao(CovidTrackerDatabase database) {
        return database.regionDao();
    }

    @RegionScope
    @Provides
    static RegionRepository providesRegionRepository(CovidTrackerRegionsApi api,
                                                     RegionDao regionDao, AppExecutors executors) {
        return new RegionRepository(api, regionDao, executors);
    }
}
