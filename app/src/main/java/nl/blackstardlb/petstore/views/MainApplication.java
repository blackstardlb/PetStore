package nl.blackstardlb.petstore.views;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;
import nl.blackstardlb.petstore.di.ApplicationComponent;
import nl.blackstardlb.petstore.di.ApplicationModule;
import nl.blackstardlb.petstore.di.DaggerApplicationComponent;

public class MainApplication extends Application {
    private static ApplicationComponent applicationComponent;

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        applicationComponent = buildComponent();
    }

    private ApplicationComponent buildComponent() {
        return DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }
}
