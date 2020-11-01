package com.fii.covidtracker.repositories.models.articles;

import com.fii.covidtracker.repositories.models.BaseModel;
import com.fii.covidtracker.repositories.models.MainListItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Article extends MainListItem {

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

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getLeftSubtitle() {
        return author;
    }

    @Override
    public String getRightSubtitle() {
        String pattern = "MM/dd/yyyy HH:mm:ss";

        DateFormat df = new SimpleDateFormat(pattern, Locale.ROOT);

        return df.format(publishDate);
    }
}
