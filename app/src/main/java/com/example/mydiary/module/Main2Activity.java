package com.example.mydiary.module;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mydiary.R;
import com.example.mydiary.databinding.ActivityMain2Binding;
import com.example.mydiary.room.Record;
import com.example.mydiary.room.RecordDao;
import com.example.mydiary.room.User;
import com.example.mydiary.room.UserDao;
import com.example.mydiary.room.UserDatabase;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    public static final int LOGIN_ACTIVITY = 1;
    public static final int CHOOSE_PICTURE = 2;
    public static final int ADD_RECORD = 3;

    private RecordAdapter recordAdapter;
    private List<Record> recordList;
    private ListView listView;
    private RelativeLayout relativeLayout;
    private ActivityMain2Binding binding;
    UserDao userDao;
    RecordDao recordDao;
    String phone;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,TAG+"--->onCreate");
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main2);

        userDao = UserDatabase.getInstance(this).getUserDao();
        recordDao = UserDatabase.getInstance(this).getRecordDao();
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        listView = (ListView) findViewById(R.id.mine_list_view);
        relativeLayout = (RelativeLayout) findViewById(R.id.show_none_record);

        createListView();
        Toast.makeText(Main2Activity.this, "Welcome " + phone,
                Toast.LENGTH_LONG).show();
        binding.myTextView.setText(phone);


        //User account = userDao.getUser(phone);
        //这里是添加头像
        Bitmap mBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.headshot)).getBitmap();
        byte[] im = img(mBitmap);
        byte[] urim = im;
        Bitmap bitmap = BitmapFactory.decodeByteArray(urim, 0, urim.length);

        Button head = (Button) findViewById(R.id.head);
        head.setBackgroundDrawable(new BitmapDrawable(bitmap));
        head.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("记录");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        Intent intent = new Intent(Main2Activity.this, AddRecordsActivity.class);
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                        break;

                    case R.id.about:
                        startActivity(new Intent(Main2Activity.this, AboutActivity.class));
                        break;
                }
                return true;
            }


        });
        // MineListView

        // Click item of ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (recordList != null) {
                    Record record = recordList.get(position);
                    Intent intent = new Intent(Main2Activity.this, MineShowRecordActivity.class);
                    intent.putExtra("click_rec", record);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
            }
        });

        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Main2Activity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main2Activity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.operation,menu);
        return true;
    }

    //显示list
    private void createListView() {
        recordList = recordDao.getUserAllRecord(phone);
        recordAdapter = new RecordAdapter(Main2Activity.this,
                R.layout.my_listview_item, (List<Record>) recordList);
        listView.setAdapter(recordAdapter);
        listView.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,TAG+"--->onStart");
        createListView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,TAG+"--->onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,TAG+"--->onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,TAG+"--->onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,TAG+"--->onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,TAG+"--->onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    phone = data.getStringExtra("phone");
                    createListView();
                    Toast.makeText(Main2Activity.this, "Welcome " + phone,
                            Toast.LENGTH_LONG).show();

                    TextView obj = (TextView) findViewById(R.id.my_textView);
                    obj.setText(phone);

                    //Record account = recordDao.getUser(phone);
                    Record record = new Record(phone);
                    byte[] urim = record.getRecordImage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(urim, 0, urim.length);

                    Button head = (Button) findViewById(R.id.head);
                    head.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    head.setVisibility(View.VISIBLE);
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case ADD_RECORD:
                createListView();
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        saveUserImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        saveUserImage(imagePath);
    }
    //保存用户头像
    private void saveUserImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            byte[] userimg = img(bitmap);


//            List<Account> accountList = LitePal
//                    .where("userName == ?", user_login)
//                    .find(Account.class,true);
//            Account account = accountList.get(0);
//            account.setUserImage(userimg);
//            account.save();

            Button head=(Button)findViewById(R.id.head);
            head.setBackgroundDrawable(new BitmapDrawable(bitmap));

            Toast.makeText(this, "Change avatar successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PICTURE);
    }

    private byte[] img(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}




