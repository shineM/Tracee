package com.zxy.tracee.module;

import android.app.Application;
import android.content.Context;

import com.zxy.tracee.App;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zxy on 16/3/28.
 */
@Module(injects = {App.class}, library = true)
public class AppModule {
    private App app;

    public AppModule(App app){
        this.app = app;

    }
    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    Context provideContext() {
        return app.getApplicationContext();
    }
}
