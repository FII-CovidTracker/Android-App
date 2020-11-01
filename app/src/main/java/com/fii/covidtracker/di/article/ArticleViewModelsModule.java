package com.fii.covidtracker.di.article;

import androidx.lifecycle.ViewModel;

import com.fii.covidtracker.di.ViewModelKey;
import com.fii.covidtracker.viewmodels.article.ArticleViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ArticleViewModelsModule {
    @Binds
    @IntoMap
    @ViewModelKey(ArticleViewModel.class)
    public abstract ViewModel bindArticleViewModel(ArticleViewModel viewModel);
}
