package com.fii.covidtracker.local_db;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fii.covidtracker.local_db.dao.articles.ArticleDao;
import com.fii.covidtracker.local_db.entities.articles.ArticleEntity;

@Database(
        entities = {
                ArticleEntity.class,
        },
        version = 1
)
@TypeConverters({Converters.class})
public abstract class CovidTrackerDatabase extends RoomDatabase {
    public abstract ArticleDao articleDao();
}
