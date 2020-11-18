package com.example.wallpapertest;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class CategoryActivity extends BaseActivity {
    private DrawerLayout mDrawerLayout;
    String UserName;
    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout2);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navView);
        if (navigationView.getHeaderCount()>0){
            View head = navigationView.getHeaderView(0);
            TextView name = (TextView)head.findViewById(R.id.username);
            TextView useremail = (TextView)head.findViewById(R.id.mail);
            Intent intent = getIntent();
            String username = intent.getStringExtra("USER_NAME");
            String email = intent.getStringExtra("EMAIL");
            UserName = username;
            Email = email;
            name.setText(username);
            useremail.setText(email);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_friend);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_task:
                        loginout();
                        break;
                    case R.id.nav_friend:
                        ToCategoryActivity(UserName,Email);
                        break;
                    case R.id.nav_favourite:
                        ToFavourite(UserName,Email);
                        break;
                    case R.id.nav_call:
                        ToMainActivity(UserName,Email);
                    default:
                        mDrawerLayout.closeDrawers();
                }
                //mDrawerLayout.closeDrawers();
                return true;
            }
        });
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

    /*
     * 退出登录
     * */
    public void loginout(){
        Intent intent = new Intent("com.example.wallpapertest.loginout");
        sendBroadcast(intent);
    }

    /*跳转到收藏界面*/
    public void ToFavourite(String userName,String email){
        Intent intent = new Intent(CategoryActivity.this,FavouriteActivity.class);
        intent.putExtra("UserName",userName);
        intent.putExtra("Email",email);
        startActivity(intent);
    }

    /*跳转到首页推荐页面*/
    public void ToMainActivity(String userName,String email){
        Intent intent = new Intent(CategoryActivity.this,MainActivity.class);
        intent.putExtra("USER_NAME",userName);
        intent.putExtra("EMAIL",email);
        startActivity(intent);
    }

    /*跳转到分类页面*/
    public void ToCategoryActivity(String userName,String email){
        Intent intent = new Intent(CategoryActivity.this,CategoryActivity.class);
        intent.putExtra("USER_NAME",userName);
        intent.putExtra("EMAIL",email);
        startActivity(intent);
    }
}