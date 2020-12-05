package com.fii.covidtracker.ui.article;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fii.covidtracker.R;
import com.fii.covidtracker.bluethoot.controls.ControlsFragment;
import com.fii.covidtracker.network.ResourceStatus;
import com.fii.covidtracker.repositories.models.articles.Article;
import com.fii.covidtracker.ui.MainListItemAdapter;
import com.fii.covidtracker.viewmodels.ViewModelProviderFactory;
import com.fii.covidtracker.viewmodels.article.ArticleViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ArticleListFragment extends DaggerFragment {
    private static final String TAG = "ArticleListActivity";

    @Inject
    ViewModelProviderFactory providerFactory;

    private ArticleViewModel articleViewModel;

    private RecyclerView articlesRecyclerView;
    private MainListItemAdapter<Article> articleAdapter;
    private List<Article> articles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        articleViewModel = new ViewModelProvider(this, providerFactory)
                .get(ArticleViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.article_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        articlesRecyclerView = getActivity().findViewById(R.id.list);
        articlesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        articleAdapter = new MainListItemAdapter<>();
        articlesRecyclerView.setAdapter(articleAdapter);

        articleAdapter.setOnClickListeners(position -> {
            if (articles != null){
                Intent intent = new Intent(this.getActivity(), ArticleInfoActivity.class);
                Bundle b = new Bundle();
                b.putInt("articleId", articles.get(position).getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        subscribeToArticles(false);
    }


    private void subscribeToArticles(boolean forceFetch) {
        articleViewModel.getArticlesResource(forceFetch).removeObservers(getActivity());
        articleViewModel.getArticlesResource(forceFetch).observe(getActivity(), articlesResource -> {
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
                        Toast.makeText(getContext(), "Can't connect to our server!", Toast.LENGTH_SHORT).show();
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

    public static ArticleListFragment newInstance() {
        return new ArticleListFragment();
    }

    private void processArticleList(ResourceStatus status) {
        //TODO: show this better
        Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
    }
}