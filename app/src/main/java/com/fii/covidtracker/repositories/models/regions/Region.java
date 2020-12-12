package com.fii.covidtracker.repositories.models.regions;

import com.fii.covidtracker.repositories.models.MainListItem;

public class Region extends MainListItem {

    public int id;
    public String name;
    public boolean global;

    public Region(int id, String name, boolean global, long createdAt, long modifiedAt) {
        this.id = id;
        this.name = name;
        this.global = global;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getLeftSubtitle() {
        return name;
    }

    @Override
    public String getRightSubtitle() {
        return global? "True":"False";
    }


    @Override
    public String toString() {
        return name;
    }
}