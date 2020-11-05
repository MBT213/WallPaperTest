package com.example.wallpapertest;

public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
}
