package com.example.wallpapertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.android.material.navigation.NavigationView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    List<WallPaper> imageArray = new ArrayList<>();
    private List<Integer> mHeight;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WallPaperAdapter adapter;
    String UserName;

    String url = "http://service.picasso.adesk.com/v1/lightwp/vertical?adult=0&first=1&limit=30&order=hot&skip=30";//图片json数据源

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            List<WallPaper> array = (ArrayList) msg.obj;
            //System.out.print(array.size());
            imageArray=array;
            Log.d("directory", String.valueOf(imageArray));
            getRandomHeight();
            adapter = new WallPaperAdapter(imageArray,mHeight,UserName);
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));//展示3列
            mRecyclerView.setAdapter(adapter);

        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            String username = intent.getStringExtra("USER_NAME");
            String email = intent.getStringExtra("EMAIL");
            UserName = username;
            name.setText(username);
            useremail.setText(email);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_call);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        init();
        /*
        * 更新图片
        * */
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }


    /*
    *更新图片
     */
    private void refreshFruits(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /*
    * 加载图片
    * */
    private void init() {

        Log.d("url", url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("-----","on failure");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("-------", "onResponse: "+response);
                        ResultModel model = JSON.parseObject(response.body().string(),ResultModel.class);
                        List<WallModel>  datas= model.getRes().getVertical();
                        List<WallPaper> wallPapers2 = new ArrayList<>();
                        for (int i=0;i<datas.size();i++){
                            wallPapers2.add(new WallPaper((String) datas.get(i).getImg()));
                            //Log.d("-------", (String) datas.get(i).getImg());
                        }
                        Message m = new Message();
                        m.obj = wallPapers2;
                        handler.sendMessage(m);
                        Log.d("------","onResponse");
                        response.body().close();
                    }
                });
            }
        }).start();
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