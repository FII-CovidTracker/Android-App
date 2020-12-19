package com.fii.covidtracker.ui.article;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fii.covidtracker.R;
import com.fii.covidtracker.network.ResourceStatus;
import com.fii.covidtracker.repositories.models.articles.Article;
import com.fii.covidtracker.viewmodels.ViewModelProviderFactory;
import com.fii.covidtracker.viewmodels.article.ArticleViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;

public class ArticleInfoActivity extends DaggerAppCompatActivity {
    private static final String TAG = "ArticleInfoActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    private ArticleViewModel articleViewModel;

    private Article article;

    private TextView title;
    private TextView markdownContent;

    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_info);
        articleViewModel = new ViewModelProvider(this, providerFactory)
                .get(ArticleViewModel.class);

        title = findViewById(R.id.title);
        markdownContent = findViewById(R.id.markdownContent);
        markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(this))
                .build();


        loadLastArticle();
        subscribeToArticle();
    }

    private void loadLastArticle() {
        Bundle b = getIntent().getExtras();
        if(b != null)
            articleViewModel.setArticleId(b.getInt("articleId"));
    }

    private void subscribeToArticle() {
        articleViewModel.getArticleResource().removeObservers(this);
        articleViewModel.getArticleResource().observe(this, articleResource -> {
            if (articleResource != null) {
                switch (articleResource.getStatus()) {
                    case LOADING:
                        if (articleResource.getData() != null) {
                            processArticle(articleResource.getData());
                        } else {
                            processArticle(ResourceStatus.LOADING);
                        }
                        break;
                    case SUCCESS:
                        if (articleResource.getData() != null) {
                            processArticle(articleResource.getData());
                        } else {
                            processArticle(ResourceStatus.EMPTY);
                        }
                        break;
                    case ERROR:
                        Toast.makeText(this, "Can't connect to our server!",
                                Toast.LENGTH_SHORT).show();
                        if (articleResource.getData() == null) {
                            processArticle(ResourceStatus.ERROR);
                        }
                        break;
                }
            }
        });
    }

    private void processArticle(Article article) {
        this.article = article;
        this.title.setText(article.title);
        Log.i(TAG, "markdownContent: " + article.markdownContent);
        if(article.markdownContent != null) {
            markwon.setMarkdown(this.markdownContent, article.markdownContent);
        }
    }

    private void processArticle(ResourceStatus status) {
        //TODO: show this better
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
    }

}