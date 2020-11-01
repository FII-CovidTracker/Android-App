package com.fii.covidtracker.di.article;

import com.fii.covidtracker.AppExecutors;
import com.fii.covidtracker.local_db.CovidTrackerDatabase;
import com.fii.covidtracker.local_db.dao.articles.ArticleDao;
import com.fii.covidtracker.network.apis.articles.CovidTrackerArticlesApi;
import com.fii.covidtracker.repositories.articles.ArticleRepository;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ArticleModule {
    @ArticleScope
    @Provides
    static ArticleDao providesArticleDao(CovidTrackerDatabase database) {
        return database.articleDao();
    }

    @ArticleScope
    @Provides
    static ArticleRepository providesArticleRepository(CovidTrackerArticlesApi api,
                                                       ArticleDao articleDao, AppExecutors executors) {
        return new ArticleRepository(api, articleDao, executors);
    }
}
