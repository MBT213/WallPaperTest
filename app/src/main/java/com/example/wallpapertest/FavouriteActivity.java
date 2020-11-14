package com.example.wallpapertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    List<WallPaper> imageArray = new ArrayList<>();
    private List<Integer> mHeight;
    private WallPaperAdapter adapter;
    private MyDatabaseHelper myDatabaseHelper;
    String UserName;
    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        myDatabaseHelper = new MyDatabaseHelper(this,"WallPaper.db",null,1);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        if (navigationView.getHeaderCount()>0){
            View head = navigationView.getHeaderView(0);
            TextView name = (TextView)head.findViewById(R.id.username);
            TextView useremail = (TextView)head.findViewById(R.id.mail);
            Intent intent = getIntent();
            String username = intent.getStringExtra("UserName");
            String email = intent.getStringExtra("Email");
            Email = email;
            //Log.d("FavoriteName",Email);
            UserName = username;
            name.setText(username);
            useremail.setText(email);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_favourite);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_task:
                        loginout();
                        break;
                    case R.id.nav_favourite:
                        ToFavourite(UserName,Email);
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                }
                //mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        initFa();
    }

    /*
    * 加载收藏的壁纸
    * */
    public void initFa(){
        myDatabaseHelper.getWritableDatabase();
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        String Query = "Select * from favourite_wallpaper where username =? ";
        Cursor cursor = db.rawQuery(Query,new String[] { UserName });
        if (cursor.moveToFirst()){
            do {
                imageArray.add(new WallPaper(cursor.getString(cursor.getColumnIndex("favourite"))));
            }while (cursor.moveToNext());
        }
        cursor.close();
        getRandomHeight();
        adapter = new WallPaperAdapter(imageArray,mHeight,UserName);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));//展示3列
        mRecyclerView.setAdapter(adapter);
    }

    /*
     * 设置随机图片高
     * */
    public void getRandomHeight() {
        //获取所有图片
        mHeight=new ArrayList<>();
        for (int i = 0; i <= imageArray.size(); i++) {
            //依次给给图片设置宽高
            mHeight.add((int)(300+Math.random()*400));
        }

    }

    /*
     * 退出登录
     * */
    public void loginout(){
        Intent intent = new Intent("com.example.wallpapertest.loginout");
        sendBroadcast(intent);
    }

    /*
    * 跳转到收藏界面
    * */
    public void ToFavourite(String userName,String email){
        Intent intent = new Intent(FavouriteActivity.this,FavouriteActivity.class);
        intent.putExtra("UserName",userName);
        intent.putExtra("Email",email);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.backcup:
                Toast.makeText(this,"you click backcup",Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this,"you click delete",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this,"you click setting",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }
}