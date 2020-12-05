package com.fii.covidtracker.di;

import com.fii.covidtracker.di.article.ArticleBuildersModule;
import com.fii.covidtracker.di.article.ArticleModule;
import com.fii.covidtracker.di.article.ArticleScope;
import com.fii.covidtracker.di.article.ArticleViewModelsModule;
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

}
