package com.fii.covidtracker.local_db.dao.articles;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.fii.covidtracker.local_db.dao.BaseDao;
import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;

import java.util.List;


@Dao
public abstract class ArticleDao extends BaseDao<ArticleEntity> {

    @Transaction
    @Query("SELECT * FROM articles WHERE id = :id")
    public abstract LiveData<ArticleEntity> getById(int id);

    @Transaction
    @Query("SELECT * FROM articles ORDER By publishDate")
    public abstract LiveData<List<ArticleEntity>> getAll();

    @Transaction
    @Query("SELECT * FROM articles ORDER By publishDate")
    public abstract List<ArticleEntity> getAllRaw();
}
