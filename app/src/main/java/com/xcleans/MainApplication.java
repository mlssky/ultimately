package com.xcleans;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

//import com.google.android.play.core.splitcompat.SplitCompat;

/**
 * Created by mengliwei on 2019-06-11.
 */
public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context var1) {
        super.attachBaseContext(var1);
//        SplitCompat.install(this);

        StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        builder.detectLeakedClosableObjects();
        StrictMode.setVmPolicy(builder.build());
    }
}
