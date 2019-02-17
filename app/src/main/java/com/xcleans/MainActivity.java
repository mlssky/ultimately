package com.xcleans;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent=new Intent();
        intent.setClass(this, tv.yixia.testj.MainActivity.class);
        startActivity(intent);

    }
}
