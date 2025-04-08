package com.example.task1.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "theme_mode")
public class ThemeModeEntity {
    @PrimaryKey
    public int id = 1;

    public boolean isDarkMode;
}