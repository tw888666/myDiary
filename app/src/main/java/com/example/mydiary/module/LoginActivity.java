package com.example.mydiary.module;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydiary.R;
import com.example.mydiary.databinding.ActivityLoginBinding;
import com.example.mydiary.room.User;
import com.example.mydiary.room.UserDao;
import com.example.mydiary.room.UserDatabase;

import org.w3c.dom.Text;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 视图绑定
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initTitle();
        userDao = UserDatabase.getInstance(this).getUserDao();

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.etPhone.getText().toString();
                String password = binding.etPassword.getText().toString();
                //获取所有用户
                List<User> arr = userDao.getAllUser();
                boolean account_flag = true;
                for(User u : arr) {
                    if(u.getPhone().equals(phone) && u.getPassword().equals(password)){
                        Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
                        intent.putExtra("phone",phone);
                        Log.i("phone:",phone);
                        startActivity(intent);
                        account_flag = false;
                        break;
                    }
                }
                if(account_flag){
                    Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ivDeletePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etPhone.setText("");
            }
        });

        binding.ivDeletePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etPassword.setText("");
            }
        });
    }


    public void startRegister(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    private void initTitle() {
        binding.layoutTitle.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}