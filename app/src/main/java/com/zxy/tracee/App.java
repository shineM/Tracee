package com.zxy.tracee;

import android.app.Application;

import com.zxy.tracee.module.AppModule;

import java.util.Arrays;

import dagger.ObjectGraph;

/**
 * Created by zxy on 16/3/28.
 */
public class App extends Application {
    private ObjectGraph objectGraph;
    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(Arrays.<Object>asList(new AppModule(this)).toArray());
        objectGraph.inject(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
