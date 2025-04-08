package com.example.task1.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ThemeModeDao {
    @Query("SELECT * FROM theme_mode WHERE id = 1")
    ThemeModeEntity getThemeMode();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setThemeMode(ThemeModeEntity themeMode);
}
