package com.fii.covidtracker.ui.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fii.covidtracker.R;
import com.fii.covidtracker.network.ResourceStatus;
import com.fii.covidtracker.repositories.models.articles.Article;
import com.fii.covidtracker.ui.MainListItemAdapter;
import com.fii.covidtracker.viewmodels.ViewModelProviderFactory;
import com.fii.covidtracker.viewmodels.article.ArticleViewModel;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ArticleListActivity extends DaggerAppCompatActivity {
    private static final String TAG = "ArticleListActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    private ArticleViewModel articleViewModel;

    private RecyclerView articlesRecyclerView;
    private MainListItemAdapter<Article> articleAdapter;
    private List<Article> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        articleViewModel = new ViewModelProvider(this, providerFactory)
                .get(ArticleViewModel.class);

        articlesRecyclerView = findViewById(R.id.list);
        articlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articleAdapter = new MainListItemAdapter<>();
        articlesRecyclerView.setAdapter(articleAdapter);

        articleAdapter.setOnClickListeners(position -> {
            if (articles != null){
                Intent intent = new Intent(ArticleListActivity.this, ArticleInfoActivity.class);
                Bundle b = new Bundle();
                b.putInt("articleId", articles.get(position).getId());
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });

        subscribeToArticles(false);
    }

    private void subscribeToArticles(boolean forceFetch) {
        articleViewModel.getArticlesResource(forceFetch).removeObservers(this);
        articleViewModel.getArticlesResource(forceFetch).observe(this, articlesResource -> {
            if (articlesResource != null) {
                switch (articlesResource.getStatus()) {
                    case LOADING:
                        if (articlesResource.getData() != null) {
                            processArticleList(articlesResource.getData());
                        } else {
                            processArticleList(ResourceStatus.LOADING);
                        }
                        break;
                    case SUCCESS:
                        if (articlesResource.getData() != null) {
                            processArticleList(articlesResource.getData());
                        } else {
                            processArticleList(ResourceStatus.EMPTY);
                        }
                        break;
                    case ERROR:
                        Toast.makeText(this, "Can't connect to our server!", Toast.LENGTH_SHORT).show();
                        if (articlesResource.getData() == null) {
                            processArticleList(ResourceStatus.ERROR);
                        }
                        break;
                }
            }
        });
    }

    private void processArticleList(List<Article> articles) {
        this.articles = articles;
        articleAdapter.submitList(articles);
        articleAdapter.notifyDataSetChanged();
    }

    private void processArticleList(ResourceStatus status) {
        //TODO: show this better
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
    }
}