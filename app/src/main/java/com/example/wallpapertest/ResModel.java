package com.example.wallpapertest;

import java.util.List;

public class ResModel {
    private boolean reviewing;
    private List<WallModel> vertical;

    public boolean isReviewing() {
        return reviewing;
    }


    public void setReviewing(boolean reviewing) {
        this.reviewing = reviewing;
    }


    public List<WallModel> getVertical() {
        return vertical;
    }

    public void setVertical(List<WallModel> vertical) {
        this.vertical = vertical;
    }
}
