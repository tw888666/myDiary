package com.example.mydiary.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecordDao {

    @Insert
    void insertRecord(Record... records);

    @Insert
    long insertSingleRecord(Record record);

    @Update
    void updateRecord(Record... records);

    @Delete
    void deleteRecord(Record... records);

    @Query("SELECT * FROM Record ORDER BY id DESC")
    List<Record> getAllRecord();

    @Query("SELECT * FROM Record WHERE userPhone = :phone")
    List<Record> getUserAllRecord(String phone);

    @Query("SELECT * FROM Record WHERE id = :id")
    Record getRecord(long id);
}
