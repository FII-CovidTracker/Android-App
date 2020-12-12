package com.fii.covidtracker.di.region;

import androidx.lifecycle.ViewModel;

import com.fii.covidtracker.di.ViewModelKey;
import com.fii.covidtracker.viewmodels.article.ArticleViewModel;
import com.fii.covidtracker.viewmodels.region.RegionViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RegionViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(RegionViewModel.class)
    public abstract ViewModel bindRegionViewModel(RegionViewModel viewModel);
}
