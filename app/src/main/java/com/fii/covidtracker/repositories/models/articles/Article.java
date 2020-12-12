package com.fii.covidtracker.repositories.models.articles;

import com.fii.covidtracker.repositories.models.MainListItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Article extends MainListItem {

    public int authorityId;
    public String author;
    public String title;
    public String markdownContent;
    public Date publishDate;

    public Article(int id, int authorityId, String author, String title,
                   String markdownContent, Date publishDate, long createdAt, long modifiedAt) {
        this.id = id;
        this.authorityId = authorityId;
        this.author = author;
        this.title = title;
        this.markdownContent = markdownContent;
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
        if(publishDate == null) {
            return "n/a";
        }
        String pattern = "MM/dd/yyyy HH:mm:ss";

        DateFormat df = new SimpleDateFormat(pattern, Locale.ROOT);

        return df.format(publishDate);
    }
}
