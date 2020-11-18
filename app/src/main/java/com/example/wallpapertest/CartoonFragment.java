package com.example.wallpapertest;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CartoonFragment extends Fragment {

    private RecyclerView mRecyclerView;
    List<WallPaper> imageArray = new ArrayList<>();
    private List<Integer> mHeight;
    private WallPaperAdapter adapter;
    String UserName;
    String Email;
    private SwipeRefreshLayout swipeRefreshLayout;
    String url = "http://service.picasso.adesk.com/v1/vertical/category/4e4d610cdf714d2966000004/vertical?limit=30&adult=false&first=1&order=new";//图片json数据源

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartoon, container, false);



        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        init();
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        UserName = ((CategoryActivity)context).UserName;
        Email = ((CategoryActivity)context).Email;
    }

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
                        List<WallModel> datas= model.getRes().getVertical();
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
                getActivity().runOnUiThread(new Runnable() {
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
}