package com.example.mydiary.module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mydiary.R;
import com.example.mydiary.databinding.ActivityRegisterBinding;
import com.example.mydiary.room.User;
import com.example.mydiary.room.UserDao;
import com.example.mydiary.room.UserDatabase;

public class RegisterActivity extends AppCompatActivity {
    UserDao userDao;
    //系统根据xml生成的绑定类
    private ActivityRegisterBinding mViewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //官方demo 视图绑定
        mViewBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = mViewBinding.getRoot();
        setContentView(view);

        initTitle();
        userDao = UserDatabase.getInstance(this).getUserDao();
        mViewBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mViewBinding.etPhone.getText().toString();
                String password = mViewBinding.etPassword.getText().toString();
                User user = new User(phone, password);
                if(userDao.getUser(phone) == null){
                    userDao.insertUser(user);
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,"手机号已注册",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mViewBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        mViewBinding.ivDeletePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.etPhone.setText("");
            }
        });

        mViewBinding.ivDeletePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.etPassword.setText("");
            }
        });
    }

    private void initTitle() {

        mViewBinding.layoutTitle.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewBinding.layoutTitle.tvTitle.setText("");
    }
}
