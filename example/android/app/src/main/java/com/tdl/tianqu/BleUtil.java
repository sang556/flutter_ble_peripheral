package com.tdl.tianqu;

import android.bluetooth.le.AdvertiseData;
import android.os.ParcelUuid;

import java.util.Timer;
import java.util.TimerTask;

public class BleUtil {

    private static Timer bluetoothRadioTimer;

    public static AdvertiseData createScanAdvertiseData(int i, byte[] bArr) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addManufacturerData(i, bArr);
        return builder.build();
    }

    public static AdvertiseData createIBeaconAdvertiseData(int i, byte[] bArr) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addServiceUuid(ParcelUuid.fromString("0000ae8f-0000-1000-8000-00805f9b34fb"));
        builder.addManufacturerData(i, bArr);
        return builder.build();
    }

    public static void send(int i) {
        BeaconManager.getInstance().stopAdvertising();
        Timer timer = bluetoothRadioTimer;
        if (timer != null) {
            timer.cancel();
            bluetoothRadioTimer = null;
        }
        switch (i) {
            case 1:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -12, 29, 124});
                break;
            case 2:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -9, -122, 78});
                break;
            case 3:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -10, 15, 95});
                break;
            case 4:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -15, -80, 43});
                break;
            case 5:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -16, 57, 58});
                break;
            case 6:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -13, -94, 8});
                break;
            case 7:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -14, 43, 25});
                break;
            case 8:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -3, -36, -31});
                break;
            case 9:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -4, 85, -16});
                break;
            default:
                BeaconManager.getInstance().startAdvertising(255, new byte[]{109, -74, 67, -50, -105, -2, 66, 124, -59, 23, 92});
                break;
        }
        stopAdvertising();
    }

    private static void stopAdvertising() {
        if (bluetoothRadioTimer == null) {
            Timer timer = new Timer();
            bluetoothRadioTimer = timer;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BeaconManager.getInstance().stopAdvertising();
                }
            }, 1000);
        }
    }
}