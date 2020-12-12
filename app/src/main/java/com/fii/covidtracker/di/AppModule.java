package com.fii.covidtracker.di;

import android.app.Application;

import androidx.room.Room;

import com.fii.covidtracker.AppExecutors;
import com.fii.covidtracker.helpers.Constants;
import com.fii.covidtracker.local_db.CovidTrackerDatabase;
import com.fii.covidtracker.network.apis.LiveDataCallAdapter;
import com.fii.covidtracker.network.apis.articles.CovidTrackerArticlesApi;
import com.fii.covidtracker.network.apis.regions.CovidTrackerRegionsApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class AppModule {
    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(Constants.COVID_TRACKER_API_URL)
                .addCallAdapterFactory(LiveDataCallAdapter.Factory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    static CovidTrackerDatabase provideCovidTrackerDatabaseInstance(Application application) {
        return Room.databaseBuilder(
                application,
                CovidTrackerDatabase.class,
                "CovidTrackerDatabase.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    static AppExecutors provideAppExecutorsInstance() {
        return new AppExecutors();
    }

    @Singleton
    @Provides
    static CovidTrackerArticlesApi provideCovidTrackerArticlesApi(Retrofit retrofit) {
        return retrofit.create(CovidTrackerArticlesApi.class);
    }

    @Singleton
    @Provides
    static CovidTrackerRegionsApi provideCovidTrackerRegionsApi(Retrofit retrofit) {
        return retrofit.create(CovidTrackerRegionsApi.class);
    }
}
