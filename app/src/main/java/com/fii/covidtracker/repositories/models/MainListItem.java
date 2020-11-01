package com.fii.covidtracker.repositories.models;

import androidx.annotation.Nullable;

public abstract class MainListItem extends BaseModel{
    public abstract String getTitle();
    public abstract String getLeftSubtitle();
    public abstract String getRightSubtitle();

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof MainListItem)){
            return false;
        }
        MainListItem other = (MainListItem)obj;
        return this.getTitle().equals(other.getTitle()) &&
                this.getLeftSubtitle().equals(other.getLeftSubtitle()) &&
                this.getRightSubtitle().equals(other.getRightSubtitle());
    }
}
