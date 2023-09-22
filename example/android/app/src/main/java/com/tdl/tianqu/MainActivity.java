package com.tdl.tianqu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;

import com.tdl.tianqu.channel.MainMethodChannel;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
    private MainMethodChannel mainMethodChannel = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏沉浸式透明（修改flutter状态栏黑色半透明为全透明）参考：https://www.136.la/android/show-21049.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0);
        }

        BinaryMessenger binaryMessenger = getFlutterEngine().getDartExecutor().getBinaryMessenger();
        mainMethodChannel = MainMethodChannel.create(this, binaryMessenger);

        BeaconManager.getInstance().setContext(this);
    }

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine){
        GeneratedPluginRegistrant.registerWith(flutterEngine);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("android", " onActivityResult  requestCode = "+ requestCode +",resultCode = " + resultCode);
        Log.e("android", " onActivityResult  data = "+ data);
        if (requestCode == 1) {

        }
    }
}
