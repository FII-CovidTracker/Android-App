package com.fii.covidtracker.network.responses.articles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ArticleResponse {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("authorityId")
    @Expose
    public int authorityId;

    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("markdown_content")
    @Expose
    public String markdownContent;

    @SerializedName("publishDate")
    @Expose
    public Date publishDate;

    public ArticleResponse(
            int id,
            int authorityId,
            String author,
            String title,
            String markdownContent,
            Date publishDate) {
        this.id = id;
        this.authorityId = authorityId;
        this.author = author;
        this.title = title;
        this.markdownContent = markdownContent;
        this.publishDate = publishDate;
    }
}
