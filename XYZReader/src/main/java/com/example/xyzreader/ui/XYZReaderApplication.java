package com.example.xyzreader.ui;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Melih Gültekin on 10.06.2018
 */
public class XYZReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}