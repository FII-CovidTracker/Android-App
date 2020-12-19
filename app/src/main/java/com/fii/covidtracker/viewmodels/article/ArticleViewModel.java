package com.fii.covidtracker.viewmodels.article;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fii.covidtracker.network.Resource;
import com.fii.covidtracker.repositories.articles.ArticleRepository;
import com.fii.covidtracker.repositories.models.articles.Article;

import java.util.List;

import javax.inject.Inject;

public class ArticleViewModel extends ViewModel {
    private static final String TAG = "ArticleViewModel";

    private ArticleRepository articleRepository;

    private LiveData<Resource<Article>> articleResource = null;
    private LiveData<Resource<List<Article>>> articlesResource = null;
    private int articleId;

    @Inject
    ArticleViewModel(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
        this.articleResource = articleRepository.getArticle(this.articleId);
    }

    public LiveData<Resource<Article>> getArticleResource() {
        if (articleResource == null){
            this.articleResource = articleRepository.getArticle(this.articleId);
        }
        return articleResource;
    }

    public LiveData<Resource<List<Article>>> getArticlesResourceForRegion(
            String regionName,
            boolean forceFetch) {
        if (articlesResource == null){
            this.articlesResource = articleRepository.getArticlesForRegion(regionName, forceFetch);
        }
        return articlesResource;
    }

    public void clearCache() {
        articleRepository.clearCache();
    }
}
