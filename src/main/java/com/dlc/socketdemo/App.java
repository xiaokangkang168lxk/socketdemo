package com.dlc.socketdemo;

import android.app.Application;

import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;

import cn.dlc.commonlibrary.okgo.OkGoWrapper;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018\6\16 0016.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        OkGoWrapper.initOkGo(this, builder.build());
        super.onCreate();
    }
}
