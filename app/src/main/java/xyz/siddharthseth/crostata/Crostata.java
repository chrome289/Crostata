package xyz.siddharthseth.crostata;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import xyz.siddharthseth.crostata.data.dao.local.preference.SharedPreferencesDao;
import xyz.siddharthseth.crostata.di.component.AppComponent;
import xyz.siddharthseth.crostata.di.component.DaggerAppComponent;
import xyz.siddharthseth.crostata.di.module.ApiModule;
import xyz.siddharthseth.crostata.di.module.AppModule;
import xyz.siddharthseth.crostata.di.module.NetworkModule;

public class Crostata extends Application implements HasAndroidInjector {

    private static AppComponent appComponent;
    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;
    @Inject
    SharedPreferencesDao sharedPreferenceDao;
/*
    @Inject
    NotificationManager notificationManager;*/

    @NotNull
    public static AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .apiModule(new ApiModule())
                .build();

        appComponent.inject(this);
        //  AppCompatDelegate.setDefaultNightMode(sharedPreferenceDao.getAppTheme());

        super.onCreate();/*
        Utils.init(this);

        createChannel(notificationManager);*/
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}