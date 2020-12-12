package com.fii.covidtracker.local_db;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fii.covidtracker.local_db.dao.articles.ArticleDao;
import com.fii.covidtracker.local_db.dao.regions.RegionDao;
import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;
import com.fii.covidtracker.local_db.entities.regions.RegionEntity;

@Database(
        entities = {
                ArticleEntity.class,
                RegionEntity.class
        },
        version = 2
)
@TypeConverters({Converters.class})
public abstract class CovidTrackerDatabase extends RoomDatabase {
    public abstract ArticleDao articleDao();
    public abstract RegionDao regionDao();
}
