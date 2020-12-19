package com.fii.covidtracker.local_db.entities.regions;

import androidx.room.Entity;
import androidx.room.Index;

import com.fii.covidtracker.local_db.entities.BaseEntity;

import java.util.Date;

@Entity(
        tableName = "regions"
)
public class RegionEntity extends BaseEntity {
    public boolean global;

    public String name;

    public RegionEntity(
            int id,
            String name,
            boolean global,
            long createdAt,
            long modifiedAt) {
        this.id = id;
        this.name = name;
        this.global = global;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
