package com.shinechain.filecashcoin.application;

import android.app.Application;

import com.shinechain.filecashcoin.http.HttpInterceptor;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zjs on 2018/12/12 15:55
 * Describe:
 */
public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //超时时间为5秒
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
