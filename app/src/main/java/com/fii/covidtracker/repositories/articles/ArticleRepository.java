package com.fii.covidtracker.repositories.articles;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;


import com.fii.covidtracker.AppExecutors;
import com.fii.covidtracker.helpers.mappers.ArticleMapper;
import com.fii.covidtracker.local_db.dao.articles.ArticleDao;
import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;
import com.fii.covidtracker.network.NetworkBoundResource;
import com.fii.covidtracker.network.Resource;
import com.fii.covidtracker.network.apis.ApiResponse;
import com.fii.covidtracker.network.apis.articles.CovidTrackerArticlesApi;
import com.fii.covidtracker.network.responses.articles.ArticleResponse;
import com.fii.covidtracker.repositories.Repository;
import com.fii.covidtracker.repositories.models.articles.Article;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class ArticleRepository extends Repository {
    private static final String TAG = "ArticleRepository";
    private CovidTrackerArticlesApi api;
    private ArticleDao articleDao;

    @Inject
    public ArticleRepository(CovidTrackerArticlesApi api,
                             ArticleDao articleDao, AppExecutors executors) {
        this.api = api;
        this.executors = executors;
        this.articleDao = articleDao;
    }

    public LiveData<Resource<Article>> getArticle(int articleId) {
        return new NetworkBoundResource<Article, ArticleResponse>(executors) {
            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Fetching the article with id " + articleId + " failed.");
            }

            @Override
            protected void saveCallResult(ArticleResponse articleResponse) {
                if (articleResponse != null) {

                    ArticleEntity articleEntity = ArticleMapper.map(articleResponse);

                    articleDao.upsert(articleEntity);

                    Log.i(TAG, "Saved the article with id " + articleId + " into our local database.");
                }
            }

            @Override
            protected boolean shouldFetch(Article article) {
                boolean fetch = ArticleRepository.this.shouldFetch(article);
                Log.i(TAG, "shouldFetch article " + (article != null ? article.id : "<<new article>>") + ": " + fetch);
                return fetch;
            }

            @Override
            protected LiveData<Article> loadFromDb() {
                return Transformations.map(articleDao.getById(articleId), ArticleMapper::map);
            }

            @Override
            protected LiveData<ApiResponse<ArticleResponse>> createCall() {
                LiveData<ApiResponse<ArticleResponse>> call = api.getArticle(articleId);
                Log.i(TAG, "Called the api for info related to the article with id " + articleId);
                return call;
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Article>>> getArticles(boolean forceFetch) {

        return new NetworkBoundResource<List<Article>, List<ArticleResponse>>(executors) {
            @Override
            protected void onFetchFailed() {
                Log.e(TAG, "Fetching articles failed.");
            }

            @Override
            protected void saveCallResult(List<ArticleResponse> articleResponses) {
                if (articleResponses != null) {

                    List<ArticleEntity> articleEntities = articleResponses
                            .stream()
                            .map(ArticleMapper::map)
                            .collect(Collectors.toList());

                    articleDao.upsert(articleEntities);
                    // Todo: modify here
                    Log.i(TAG, "Saved the articles into our local database.");
                }
            }

            @Override
            protected boolean shouldFetch(List<Article> articles) {
                boolean fetch = ArticleRepository.this.shouldFetch(articles);
                Log.i(TAG, "shouldFetch articles: " + fetch);
                return forceFetch || fetch;
            }

            @Override
            protected LiveData<List<Article>> loadFromDb() {
                return Transformations.map(articleDao.getAll(), ArticleMapper::map);
            }

            @Override
            protected LiveData<ApiResponse<List<ArticleResponse>>> createCall() {
                LiveData<ApiResponse<List<ArticleResponse>>> call = api.getArticles();
                Log.i(TAG, "Called the api for articles with");
                return call;
            }
        }.asLiveData();
    }
}
