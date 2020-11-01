package com.fii.covidtracker.helpers.mappers;

import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;
import com.fii.covidtracker.network.responses.articles.ArticleResponse;
import com.fii.covidtracker.repositories.models.articles.Article;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleMapper {
    private static final String TAG = "ArticleMapper";

    public static ArticleEntity map(ArticleResponse articleResponse) {
        return new ArticleEntity(
                articleResponse.id,
                articleResponse.authorityId,
                articleResponse.author,
                articleResponse.title,
                articleResponse.markdownContent,
                articleResponse.publishDate,
                0,
                0
        );
    }

    public static Article map(ArticleEntity articleEntity) {
        return new Article(
                articleEntity.id,
                articleEntity.authorityId,
                articleEntity.author,
                articleEntity.title,
                articleEntity.markdownContent,
                articleEntity.publishDate,
                articleEntity.createdAt,
                articleEntity.modifiedAt
        );
    }

    public static List<Article> map(List<ArticleEntity> articleEntities) {
        return articleEntities
                .stream()
                .map(ArticleMapper::map)
                .collect(Collectors.toList());
    }
}
