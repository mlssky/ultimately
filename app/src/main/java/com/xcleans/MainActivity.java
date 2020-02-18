package com.xcleans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_actiivty);
        findViewById(R.id.testTxt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TestActiivty.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
