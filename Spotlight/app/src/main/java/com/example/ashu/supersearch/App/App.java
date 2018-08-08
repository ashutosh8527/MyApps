package com.example.ashu.supersearch.App;

import android.graphics.drawable.Drawable;

public class App {
    String appName,packageName;
    Drawable icon;

    public App(String appName, String packageName, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}