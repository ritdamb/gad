package it.unisa.gad.seriestracker;

import android.app.Application;

import it.unisa.gad.seriestracker.util.ApplicationVariables;


public class MyApplication extends Application {


    public void onCreate() {
        super.onCreate();
        // Init dei Singleton
        initSingletons();
    }

    protected void initSingletons() {
        ApplicationVariables.initInstance();
    }

}
