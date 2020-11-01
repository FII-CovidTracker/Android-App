package com.fii.covidtracker.repositories.models.articles;

import com.fii.covidtracker.repositories.models.BaseModel;

import java.util.Date;

public class Article extends BaseModel {

    public int authorityId;
    public String author;
    public String title;
    public String markdown_content;
    public Date publishDate;

    public Article(int id, int authorityId, String author, String title,
                   String markdown_content, Date publishDate, long createdAt, long modifiedAt) {
        this.id = id;
        this.authorityId = authorityId;
        this.author = author;
        this.title = title;
        this.markdown_content = markdown_content;
        this.publishDate = publishDate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
