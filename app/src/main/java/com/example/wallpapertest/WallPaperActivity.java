package com.example.wallpapertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WallPaperActivity extends AppCompatActivity {
    public  static final String WALLPAPER_IMAGE = "wallpaper_image";

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_paper);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        Intent intent = getIntent();
        final String wallpaperImage = intent.getStringExtra(WALLPAPER_IMAGE);
        Log.d("imgdirecory",wallpaperImage);
        ImageView imageView = (ImageView)findViewById(R.id.wallpaper_Image);
        Button download = (Button)findViewById(R.id.download_button);
        Button dummy = (Button)findViewById(R.id.dummy_button);
        Intent intent1 = new Intent(this,DownloadService.class);
        startService(intent1);
        bindService(intent1,connection,BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(WallPaperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WallPaperActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsingtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Glide.with(this).load(wallpaperImage).into(imageView);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadBinder == null){
                    return;
                }
                verifyStoragePermissions(WallPaperActivity.this);
                downloadBinder.startDownload(wallpaperImage);
            }
        });
        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verifyStoragePermissions(WallPaperActivity.this);
                    setWallpaperImage(wallpaperImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setWallpaperImage(String url) throws IOException {

        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("update_picture","fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte b[]=response.body().bytes();
                final Bitmap bm= BitmapFactory.decodeByteArray(b,0,b.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setWallpaper(bm);
                            Toast.makeText(WallPaperActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限无法访问",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    /**
     8      * Checks if the app has permission to write to device storage
     9      *
     10      * If the app does not has permission then the user will be prompted to
     11      * grant permissions
     12      *
     13      * @param activity
     14      */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}