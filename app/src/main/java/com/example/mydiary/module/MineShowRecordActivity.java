package com.example.mydiary.module;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydiary.R;
import com.example.mydiary.databinding.ActivityShowRecordBinding;
import com.example.mydiary.room.Record;
import com.example.mydiary.room.RecordDao;
import com.example.mydiary.room.User;
import com.example.mydiary.room.UserDao;
import com.example.mydiary.room.UserDatabase;


public class MineShowRecordActivity extends AppCompatActivity {

    private static final String TAG = "MineShowRecordActivity";
    private ActivityShowRecordBinding binding;
    UserDao userDao;
    Record record;

    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //视图绑定
        binding  = ActivityShowRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        Log.i("phone",phone);
        userDao = UserDatabase.getInstance(this).getUserDao();

        Toolbar toolbar = binding.showRecToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Traveller");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.update_record:
                        Intent intent = new Intent(MineShowRecordActivity.this, AddRecordsActivity.class);
                        Log.i("phone",String.valueOf(record.getUserPhone()));
                        intent.putExtra("record_id", record.getId());
                        Log.i("id", String.valueOf(record.getId()));
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.delete_record:
                        AlertDialog.Builder dialog = new AlertDialog.Builder (MineShowRecordActivity.this);
                        dialog.setTitle("Warning...");
                        dialog.setMessage("Are you sure you want to DELETE this record ?");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("Yes", (dialog1, which) -> {
                            UserDatabase.getInstance(MineShowRecordActivity.this).getRecordDao().deleteRecord(record);
                            Toast.makeText(MineShowRecordActivity.this, "Delete successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MineShowRecordActivity.this, "Cancel",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        record = (Record) intent.getSerializableExtra("click_rec");
        String date = record.getRecordDate();
        //显示不对！删除没成功！
        if(date == null) {
            date = "";
        }
        binding.showRecordDate.setText(date);
        binding.showRecordLoc.setText(record.getRecordLocation());
        binding.showRecordTitle.setText(record.getRecordTitle());
        binding.showRecordText.setText(record.getRecordText());
        binding.showRecordText.setMovementMethod(ScrollingMovementMethod.getInstance());
        //show_record_image
        byte[] im = record.getRecordImage();
        if (im != null && im.length != 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(im, 0, im.length);
            binding.showRecordImage.setImageBitmap(bitmap);
        }
        else{
            binding.showRecordImage.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.process_record,menu);
        return true;
    }
}
