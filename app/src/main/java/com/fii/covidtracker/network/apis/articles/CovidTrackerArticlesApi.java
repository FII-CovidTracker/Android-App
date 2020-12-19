package com.fii.covidtracker.network.apis.articles;

import androidx.lifecycle.LiveData;

import com.fii.covidtracker.network.apis.ApiResponse;
import com.fii.covidtracker.network.responses.articles.ArticleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CovidTrackerArticlesApi {

    @GET("article/{articlesId}")
    LiveData<ApiResponse<ArticleResponse>> getArticle(@Path("articlesId") int articlesId);

    @GET("article")
    LiveData<ApiResponse<List<ArticleResponse>>> getArticles();

    @GET("article/getByRegion")
    LiveData<ApiResponse<List<ArticleResponse>>> getArticlesForRegion(
            @Query("region")String region);

}
