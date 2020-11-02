package com.example.wallpapertest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WallPaperAdapter extends RecyclerView.Adapter<WallPaperAdapter.ViewHolder>{
    private Context mcontext;
    private List<WallPaper> mwallpaperList;
    private List<Integer> mHeight;

    public WallPaperAdapter(List<WallPaper> wallPaperList,List<Integer> Height){
        mwallpaperList = wallPaperList;
        mHeight = Height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mcontext == null){
            mcontext = parent.getContext();
        }
        View view = LayoutInflater.from(mcontext).inflate(R.layout.wallpaper_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) holder.wallpaperImage.getLayoutParams();
        //设置高
        params.height=mHeight.get(position);
        holder.wallpaperImage.setLayoutParams(params);
        WallPaper wallPaper = mwallpaperList.get(position);
        Glide.with(mcontext).load(wallPaper.getImageId()).into(holder.wallpaperImage);
    }

    @Override
    public int getItemCount() {
        return mwallpaperList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView wallpaperImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            wallpaperImage = (ImageView)itemView.findViewById(R.id.wallpaper_Image);
        }
    }
}
