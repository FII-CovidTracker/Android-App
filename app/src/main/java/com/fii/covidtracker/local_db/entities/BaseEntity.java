package com.fii.covidtracker.local_db.entities;

import androidx.room.PrimaryKey;

public abstract class BaseEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long createdAt;

    public long modifiedAt;
}
