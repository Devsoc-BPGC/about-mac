package com.macbitsgoa.about;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import io.realm.Realm;

/**
 * @author Rushikesh Jogdand.
 */
public class AboutMac extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Fresco.initialize(this);
    }
}
