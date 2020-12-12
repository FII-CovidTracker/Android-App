package com.fii.covidtracker.di;

import com.fii.covidtracker.MainActivity;
import com.fii.covidtracker.bluethoot.controls.ControlsFragment;
import com.fii.covidtracker.di.article.ArticleBuildersModule;
import com.fii.covidtracker.di.article.ArticleModule;
import com.fii.covidtracker.di.article.ArticleScope;
import com.fii.covidtracker.di.article.ArticleViewModelsModule;
import com.fii.covidtracker.di.region.RegionBuildersModule;
import com.fii.covidtracker.di.region.RegionModule;
import com.fii.covidtracker.di.region.RegionScope;
import com.fii.covidtracker.di.region.RegionViewModelsModule;
import com.fii.covidtracker.repositories.models.regions.Region;
import com.fii.covidtracker.ui.article.ArticleInfoActivity;
import com.fii.covidtracker.ui.article.ArticleListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class AppBuildersModule {

    @ArticleScope
    @ContributesAndroidInjector(
            modules = {ArticleBuildersModule.class,
                    ArticleViewModelsModule.class,
                    ArticleModule.class
            }
    )
    abstract ArticleListFragment contributeArticleListActivity();

    @ArticleScope
    @ContributesAndroidInjector(
            modules = {ArticleBuildersModule.class,
                    ArticleViewModelsModule.class,
                    ArticleModule.class
            }
    )
    abstract ArticleInfoActivity contributeArticleInfoActivity();

    @RegionScope
    @ContributesAndroidInjector(
            modules = {RegionBuildersModule.class,
                    RegionViewModelsModule.class,
                    RegionModule.class
            }
    )
    abstract ControlsFragment contributeControlsFragment();

}
