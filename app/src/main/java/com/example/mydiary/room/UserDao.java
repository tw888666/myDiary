package com.example.mydiary.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao //Database Access Object
public interface UserDao {


    @Insert
    void insertUser(User... users);

    @Update
    void updateUser(User... users);

    @Delete
    void deleteUser(User... users);

    //删除所有 @Delete 单个条件删除用的
    @Query("DELETE FROM User")
    void deleteAllUser();

    //查询所有
    @Query("SELECT * FROM User ORDER BY Id DESC")
    List<User> getAllUser();


    @Query("SELECT * FROM User WHERE phone = :phone")
    User getUser(String phone);

}
