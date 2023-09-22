package com.tdl.tianqu.channel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.nirjon.bledemo4_advertising.util.BLEUtil;
import com.tdl.tianqu.BleUtil;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainMethodChannel implements MethodChannel.MethodCallHandler {

    private static final String MAIN_CHANNEL_NAME = "com.tdl/ble";
    private Activity _activity;

    private MainMethodChannel(Activity activity, BinaryMessenger binaryMessenger) {
        _activity = activity;
        MethodChannel methodChannel = new MethodChannel(binaryMessenger, MAIN_CHANNEL_NAME);
        methodChannel.setMethodCallHandler(this);
    }

    public static MainMethodChannel create(Activity activity, BinaryMessenger binaryMessenger) {
        return new MainMethodChannel(activity, binaryMessenger);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getBleCommand":
                byte[] buffer = BLEUtil.getBleCommand(call.arguments.toString());
                result.success(buffer);
                break;
            case "sendCmd":
                BleUtil.send(Integer.parseInt(call.arguments.toString()));
                result.success("success");
                break;
            default:
                result.notImplemented();
        }
    }
}
