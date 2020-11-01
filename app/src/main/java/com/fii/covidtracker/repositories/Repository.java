package com.fii.covidtracker.repositories;

import com.fii.covidtracker.helpers.Constants;
import com.fii.covidtracker.repositories.models.BaseModel;

import java.util.List;

public abstract class Repository {
    protected com.fii.covidtracker.AppExecutors executors;

    protected boolean shouldFetch(BaseModel model) {
        if (model != null) {
            return System.currentTimeMillis() - model.modifiedAt > Constants.getCacheTTLMilis();
        }
        return true;
    }

    protected boolean shouldFetch(List<? extends BaseModel> models) {
        if (models != null && models.size() > 0) {
            boolean fetch = false;
            for (BaseModel model : models) {
                fetch = fetch || shouldFetch(model);
            }
            return fetch;
        }
        return true;
    }
}
