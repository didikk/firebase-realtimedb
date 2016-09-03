package me.didik.realtimedb;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by didik on 01/09/16.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
