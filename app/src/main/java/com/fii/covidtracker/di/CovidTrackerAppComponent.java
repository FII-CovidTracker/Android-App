package com.fii.covidtracker.di;

import android.app.Application;

import com.fii.covidtracker.CovidTrackerApp;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppBuildersModule.class,
        AppModule.class,
        ViewModelFactoryModule.class
})
public interface CovidTrackerAppComponent
        extends AndroidInjector<CovidTrackerApp> {
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        CovidTrackerAppComponent build();
    }
}