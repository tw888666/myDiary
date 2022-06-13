package com.example.mydiary.room;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

//一张表
@Entity(indices = {@Index(value = "phone",unique = true)}
)
public class User implements Serializable {

    //主键
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String phone;

    private String password;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
