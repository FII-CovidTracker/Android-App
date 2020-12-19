package com.fii.covidtracker.local_db.entities.articles;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import com.fii.covidtracker.local_db.entities.BaseEntity;

import java.util.Date;

@Entity(
        tableName = "articles",
        indices = {
                @Index("authorityId")
        }
)
public class ArticleEntity extends BaseEntity {

    public int authorityId;

    public String author;

    public String title;

    public String markdownContent;

    public Date publishDate;

    public ArticleEntity(
            int id,
            int authorityId,
            String author,
            String title,
            String markdownContent,
            Date publishDate,
            long createdAt,
            long modifiedAt) {
        this.id = id;
        this.authorityId = authorityId;
        this.author = author;
        this.title = title;
        this.markdownContent = markdownContent;
        this.publishDate = publishDate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}