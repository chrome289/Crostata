package xyz.siddharthseth.crostata;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import xyz.siddharthseth.crostata.data.model.LoggedSubject;

public class Crostata extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LoggedSubject.INSTANCE.init(getApplicationContext());
        // Fabric.with(this, new Crashlytics());
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
