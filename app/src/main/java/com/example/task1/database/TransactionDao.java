package com.example.task1.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.task1.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Transaction> transactions);

    @Query("SELECT * FROM transactions")
    List<Transaction> getAllTransactions();

    @Query("DELETE FROM transactions")
    void deleteAll();
}
