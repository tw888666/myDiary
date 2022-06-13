package com.example.mydiary.room;


import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.mydiary.room.User;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "phone",
        childColumns = "userPhone",
        onDelete = CASCADE
),
        indices = {@Index("userPhone")}
)

public class Record implements Serializable {
    //主键
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String userPhone;

    private String recordTitle;

    private String recordText;

    private String recordDate;

    private String recordLocation;

    private byte[] recordImage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRecordTitle() {
        return recordTitle;
    }

    public void setRecordTitle(String recordTitle) {
        this.recordTitle = recordTitle;
    }

    public String getRecordText() {
        return recordText;
    }

    public void setRecordText(String recordText) {
        this.recordText = recordText;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordLocation() {
        return recordLocation;
    }

    public void setRecordLocation(String recordLocation) {
        this.recordLocation = recordLocation;
    }

    public byte[] getRecordImage() {
        return recordImage;
    }

    public void setRecordImage(byte[] recordImage) {
        this.recordImage = recordImage;
    }

    public Record(String userPhone) {
        this.userPhone = userPhone;
    }

    //public Record() {;}
}
