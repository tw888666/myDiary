package com.example.mydiary.module;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.example.mydiary.R;
import com.example.mydiary.databinding.ActivityAddRecordsBinding;
import com.example.mydiary.room.Record;
import com.example.mydiary.room.RecordDao;
import com.example.mydiary.room.UserDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddRecordsActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PICTURE = 2;
    private static final String TAG = "AddRecordActivity";
    RecordDao recordDao;
    private ImageView picture;
    private Uri imageUri;
    private String location;
    private boolean useTitle = false;
    private boolean getImage = false;
    private boolean getLocation = false;
    private boolean recordHasLoc = false;

    private long recordId;
    public TextView record_show_loc;
    String phone;
    public LocationClient mLocationClient =null;
    private ActivityAddRecordsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //视图绑定
        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        binding = ActivityAddRecordsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LocationClient.setAgreePrivacy(true);
        EditText record_text = binding.RecText;
        EditText record_title = binding.RecTitle;
        //final?
        Button record_location = binding.RecLocation;
        Button record_clear_location = binding.RecClearLocation;
        Button record_show_map = binding.RecShowMap;
        record_show_loc = binding.RecShowLocation;
        picture = binding.picture;
        final CheckBox use_title = binding.useTitle;
        //bar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        recordId = intent.getLongExtra("record_id",0);
        Log.i("recordId", String.valueOf(recordId));
        phone = intent.getStringExtra("phone");
        //获取Dao
        recordDao = UserDatabase.getInstance(this).getRecordDao();
        // update record 更新记录时显示记录表
        if(recordId != 0){
            //修改记录更新记录表
            LinearLayout loc_layout = binding.AddLocationLayout;
            loc_layout.setVisibility(View.GONE);
            Record record = recordDao.getRecord(recordId);
            String rec_title = record.getRecordTitle();
            String rec_text = record.getRecordText();
            String rec_loc = record.getRecordLocation();

            if(!rec_title.isEmpty()){
                use_title.setChecked(true);
                useTitle = true;
                record_title.setVisibility(View.VISIBLE);
                record_title.setText(rec_title);
            }

            record_text.setText(rec_text);

            if(rec_loc.isEmpty()){
                recordHasLoc = false;
            }
            else {
                recordHasLoc = true;
            }

            byte[] im = record.getRecordImage();
            if (im != null && im.length != 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(im, 0, im.length);
                picture.setImageBitmap(bitmap);
                getImage = true;
            }
            else {
                getImage = false;
            }
        }

        //添加记录和修改记录的ok按钮
        binding.RecCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordId != 0){
                    //修改记录
                    Record record = recordDao.getRecord(recordId);
                    String up_text = record_text.getText().toString();
                    String up_title = record_title.getText().toString();
                    if (up_text.isEmpty() && (up_title.isEmpty()||!useTitle) && !getImage && !recordHasLoc) {
                        Toast.makeText(AddRecordsActivity.this, "Not allowed to be ALL EMPTY.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(useTitle) {
                        if(up_title.equals("")){
                            Toast.makeText(AddRecordsActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        record.setRecordTitle(up_title);
                    }
                    else {
                        record.setRecordTitle("");
                    }
                    record.setRecordText(up_text);
                    if(getImage) {
                        picture.setDrawingCacheEnabled(true);
                        Bitmap bitmap = picture.getDrawingCache();
                        byte[] image = img(bitmap);
                        record.setRecordImage(image);
                        picture.setDrawingCacheEnabled(false);
                    }
                    else {
                        record.setRecordImage(null);
                    }
                    //修改记录
                    recordDao.updateRecord(record);
                    Toast.makeText(AddRecordsActivity.this, "Update record successfully.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    // 添加新记录
                    Record record = new Record(phone);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());

                    String date = formatter.format(curDate);
                    String text = record_text.getText().toString();
                    String title = record_title.getText().toString();

                    if (text.isEmpty() && title.isEmpty() && !getImage && !getLocation) {
                        Toast.makeText(AddRecordsActivity.this, "Not allowed to be ALL EMPTY.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (useTitle) {
                        if(title.equals("")) {
                            Toast.makeText(AddRecordsActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        record.setRecordTitle(title);
                    } else {
                        record.setRecordTitle("");
                    }
                    record.setRecordText(text);
                    record.setRecordDate(date);
                    if (getLocation) {
                        record.setRecordLocation(location);
                    }
                    else {
                        record.setRecordLocation("");
                    }
                    if (getImage) {
                        picture.setDrawingCacheEnabled(true);
                        Bitmap bitmap = picture.getDrawingCache();
                        byte[] image = img(bitmap);
                        record.setRecordImage(image);
                        picture.setDrawingCacheEnabled(false);
                    }
                    //插入record
                    long rowId = recordDao.insertSingleRecord(record);
                    if(rowId > 0){
                        Toast.makeText(AddRecordsActivity.this,"添加成功！",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(AddRecordsActivity.this,"添加失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Button of get location
        record_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mLocationClient = new LocationClient(getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(mLocationClient == null){
                    Log.i(TAG, "onClick: yes");
                }else{
                    Log.i(TAG, "onClick: no");
                }
                mLocationClient.registerLocationListener(new MyLocationListener());
                //?
                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(AddRecordsActivity.this, Manifest.
                        permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(AddRecordsActivity.this, Manifest.
                        permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(AddRecordsActivity.this, Manifest.
                        permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(AddRecordsActivity.this, permissions, 2);
                    getLocation = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TextView blank = (TextView)findViewById(R.id.blank_location);
                            //record_show_loc.setText(location);
                            record_location.setVisibility(View.GONE);
                            record_show_map.setVisibility(View.VISIBLE);
                            blank.setVisibility(View.VISIBLE);
                            record_clear_location.setVisibility(View.VISIBLE);
                        }
                    }, 1500);
                } else {
                    requestLocation();
                    getLocation = true;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TextView blank = (TextView)findViewById(R.id.blank_location);
                            //record_show_loc.setText(location);
                            record_location.setVisibility(View.GONE);
                            record_show_map.setVisibility(View.VISIBLE);
                            blank.setVisibility(View.VISIBLE);
                            record_clear_location.setVisibility(View.VISIBLE);
                        }
                    }, 500);
                }
            }
        });

        // Button of clear Location
        record_clear_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!location.isEmpty()){
                    Toast.makeText(AddRecordsActivity.this, "Clear location",
                            Toast.LENGTH_SHORT).show();
                }
                location = "";
                getLocation = false;
                record_show_loc.setText(location);
            }
        });

        // Button of show Map
        record_show_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_map = new Intent(AddRecordsActivity.this, MapActivity.class);
                startActivity(intent_map);
            }
        });

        // Button of take a Photo
        binding.RecTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a File Object to store the photo
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(AddRecordsActivity.this,
                            "com.example.mydiary.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        // Button of clear picture
        binding.RecClearPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getImage){
                    Toast.makeText(AddRecordsActivity.this, "Clear image",
                            Toast.LENGTH_SHORT).show();
                }
                getImage = false;
                picture.setImageBitmap(null);
            }
        });
        //Diary有更好的
        // Button of choose a Picture
        binding.RecChoosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String write=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                String read=Manifest.permission.READ_EXTERNAL_STORAGE;

                final String[] WriteReadPermission = new String[] {write, read};

                int checkWrite= ContextCompat.checkSelfPermission(AddRecordsActivity.this,write);
                int checkRead= ContextCompat.checkSelfPermission(AddRecordsActivity.this,read);
                int ok=PackageManager.PERMISSION_GRANTED;

                if (checkWrite!= ok && checkRead!=ok){
                    //申请权限，读和写都申请一下，不然容易出问题
                    ActivityCompat.requestPermissions(AddRecordsActivity.this,WriteReadPermission,1);
                }else openAlbum();
//                if (ContextCompat.checkSelfPermission(AddRecordsActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
//                        PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(AddRecordsActivity.this, new
//                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                } else {
//                    openAlbum();
//                }
            }
        });

        use_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (use_title.isChecked()) {
                    record_title.setVisibility(View.VISIBLE);
                    useTitle = true;
                } else {
                    record_title.setVisibility(View.INVISIBLE);
                    record_title.setText("");
                    useTitle = false;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // show the photo
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        getImage = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PICTURE:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
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
            Log.i("context",imagePath);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
            Log.i(TAG, imagePath);
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }
    //baocuo
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
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
    // 有问题
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            getImage = true;
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PICTURE);
    }

    private byte[] img(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
//        option.setScanSpan(50000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation loc) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPosition = new StringBuilder();

                    //currentPosition.append("国家：").append(location.getCountry()).append("\n");
                    currentPosition.append(loc.getProvince()).append(" ");
                    currentPosition.append(loc.getCity()).append(" ");
                    currentPosition.append(loc.getDistrict()).append(" ");
                    currentPosition.append(loc.getStreet());
                    /*
                    currentPosition.append("定位方式：");
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {
                        currentPosition.append("GPS");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        currentPosition.append("网络");
                    }
                    */
                    String null_location = "null null null null";
                    if(null_location.equals(currentPosition + "")){
                        location = "定位失败";
                        getLocation = false;
                    }
                    else {
                        location = currentPosition.toString();
                    }
                    record_show_loc.setText(location);
                }
            });
        }
    }
}