package com.zxy.tracee.module;

import android.content.Context;

import com.zxy.tracee.ui.AddDiaryActivity;
import com.zxy.tracee.ui.DiaryDetailActivity;
import com.zxy.tracee.ui.MainActivity;

import net.tsz.afinal.FinalDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zxy on 16/3/28.
 */
@Module(injects = {
        MainActivity.class,
        DiaryDetailActivity.class,
        AddDiaryActivity.class
      },library = true,addsTo = AppModule.class
)
public class DataModule {
        @Provides @Singleton
        FinalDb.DaoConfig provideDaoConfig(Context context) {
                FinalDb.DaoConfig config = new FinalDb.DaoConfig();
                config.setDbName("diary.db");
                config.setDbVersion(1);
                config.setDebug(true);
                config.setContext(context);
                return config;
        }

        @Provides @Singleton
        FinalDb provideFinalDb(FinalDb.DaoConfig config) {
                return FinalDb.create(config);
        }
}
