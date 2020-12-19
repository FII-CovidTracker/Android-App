package com.fii.covidtracker.di;

import androidx.lifecycle.ViewModelProvider;

import com.fii.covidtracker.viewmodels.ViewModelProviderFactory;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Singleton
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(
            ViewModelProviderFactory viewModelProviderFactory);
}
