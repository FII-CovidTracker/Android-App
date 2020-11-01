package com.fii.covidtracker.local_db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fii.covidtracker.local_db.entities.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Dao
public abstract class BaseDao<Entity extends BaseEntity> {

    private static final String TAG = "BaseDao";

    @Delete
    public abstract void delete(Entity entity);

    @Delete
    public abstract void delete(List<Entity> entities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract long _insert(Entity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract List<Long> _insert(List<Entity> entities);

    @Update
    protected abstract void _update(Entity entity);

    @Update
    protected abstract void _update(List<Entity> entities);

    @Transaction
    public void update(Entity entity) {
        if (entity != null) {
            entity.modifiedAt = System.currentTimeMillis();
            _update(entity);
        }
    }

    @Transaction
    public void update(List<Entity> entities) {
        entities = entities
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        entities.forEach(entity -> {
            entity.modifiedAt = System.currentTimeMillis();
        });
        _update(entities);
    }

    @Transaction
    public long insert(Entity entity) {
        if (entity != null) {
            entity.createdAt = System.currentTimeMillis();
            entity.modifiedAt = System.currentTimeMillis();
            return _insert(entity);
        } else {
            return -1;
        }
    }

    @Transaction
    public List<Long> insert(List<Entity> entities) {
        entities = entities
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        entities.forEach(entity -> {
            entity.createdAt = System.currentTimeMillis();
            entity.modifiedAt = System.currentTimeMillis();
        });
        return _insert(entities);
    }

    @Transaction
    public void upsert(Entity entity) {
        if (entity != null) {
            long id = insert(entity);
            if (id == -1) {
                update(entity);
            }
        }
    }

    @Transaction
    public void upsert(List<Entity> entities) {
        List<Long> insertResult = insert(entities);
        List<Entity> updateList = new ArrayList<>();

        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1 && entities.get(i) != null) {
                updateList.add(entities.get(i));
            }
        }

        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }
}
