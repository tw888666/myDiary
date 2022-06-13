package com.example.mydiary.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class,Record.class},version = 5,exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {

    //用户只需要操作DAO
    public abstract UserDao getUserDao();
    public abstract RecordDao getRecordDao();
    public static final String DB_NAME = "user_database";
    //单例模式
    private static UserDatabase INSTANCE;
    public static synchronized UserDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context);
        }
        return INSTANCE;
    }

    private static UserDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                UserDatabase.class,
                DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

    }

}
