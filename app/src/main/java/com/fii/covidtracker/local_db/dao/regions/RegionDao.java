package com.fii.covidtracker.local_db.dao.regions;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.fii.covidtracker.local_db.dao.BaseDao;
import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;
import com.fii.covidtracker.local_db.entities.regions.RegionEntity;

import java.util.List;

@Dao
public abstract class RegionDao extends BaseDao<RegionEntity> {

    @Transaction
    @Query("SELECT * FROM regions WHERE id = :id")
    public abstract LiveData<RegionEntity> getById(int id);

    @Transaction
    @Query("SELECT * FROM regions ORDER By name")
    public abstract LiveData<List<RegionEntity>> getAll();
}
