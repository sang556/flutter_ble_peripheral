package com.tdl.tianqu;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BeaconManager {
    public static final String TAG = "联网 lock_mock_manager";
    private static BeaconManager instance;
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {

        public void onStartSuccess(AdvertiseSettings advertiseSettings) {
            super.onStartSuccess(advertiseSettings);
            if (advertiseSettings != null) {
                Log.e(BeaconManager.TAG, "onStartSuccess TxPowerLv=" + advertiseSettings.getTxPowerLevel() + " mode=" + advertiseSettings.getMode() + " timeout=" + advertiseSettings.getTimeout());
                return;
            }
            Log.e(BeaconManager.TAG, "onStartSuccess, settingInEffect is null");
        }

        public void onStartFailure(int i) {
            super.onStartFailure(i);
            Log.e(BeaconManager.TAG, "onStartFailure errorCode=" + i);
            if (i == 18) {
                Toast.makeText(BeaconManager.this.mContext, "Failed to start advertising", Toast.LENGTH_LONG).show();
            }
            if (i == 1) {
                Log.e(BeaconManager.TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (i == 2) {
                Log.e(BeaconManager.TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (i == 3) {
                Log.e(BeaconManager.TAG, "Failed to start advertising as the advertising is already started");
            } else if (i == 4) {
                Log.e(BeaconManager.TAG, "Operation failed due to an internal error");
            } else if (i == 5) {
                Log.e(BeaconManager.TAG, "This feature is not supported on this platform");
            }
            BeaconManager.this.stopAdvertising();
        }
    };
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothAdvertiser;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothManager mBluetoothManager;
    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context;
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        BluetoothManager bluetoothManager = this.mBluetoothManager;
        if (bluetoothManager == null) {
            Toast.makeText(context, "不支持ble", Toast.LENGTH_LONG).show();
            return;
        }
        if (bluetoothManager != null && this.mBluetoothAdapter == null) {
            this.mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(context, "不支持ble", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            this.mBluetoothGattServer = this.mBluetoothManager.openGattServer(context, new BluetoothGattServerCallback() {
                /* class com.maxhom.weibu.common.bluetooth.BeaconManager.AnonymousClass1 */

                public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i2) {
                    super.onConnectionStateChange(bluetoothDevice, i, i2);
                }
            });
        }
    }

    public static synchronized BeaconManager getInstance() {
        BeaconManager beaconManager;
        synchronized (BeaconManager.class) {
            if (instance == null) {
                instance = new BeaconManager();
            }
            beaconManager = instance;
        }
        return beaconManager;
    }

    public void init(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            // EasyPermissions.requestPermissions(activity, activity.getString(R.string.privacy_tip), 123, "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION");
        }
        if (!activity.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(activity, "不支持ble", Toast.LENGTH_LONG).show();
            activity.finish();
            return;
        }
        BluetoothAdapter adapter = ((BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        this.mBluetoothAdapter = adapter;
        if (adapter == null) {
            Toast.makeText(activity, "不支持ble", Toast.LENGTH_LONG).show();
            activity.finish();
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        adapter.setName("wbkj");
        BluetoothLeAdvertiser bluetoothLeAdvertiser = this.mBluetoothAdapter.getBluetoothLeAdvertiser();
        this.mBluetoothAdvertiser = bluetoothLeAdvertiser;
        if (bluetoothLeAdvertiser == null) {
            Toast.makeText(activity, "the device not support peripheral", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "the device not support peripheral");
            activity.finish();
            return;
        }
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            activity.startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), i);
        }
    }

    public void startAdvertising(int i, byte[] bArr) {
        BluetoothAdapter bluetoothAdapter;
        BluetoothLeAdvertiser bluetoothLeAdvertiser = this.mBluetoothAdvertiser;
        Log.e("startAdvertising", "bluetoothLeAdvertiser is null? " + (bluetoothLeAdvertiser == null));
        if (bluetoothLeAdvertiser != null) {
            try {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                bluetoothLeAdvertiser.stopAdvertising(this.mAdvCallback);
            } catch (Exception unused) {
            }
        }
        if (this.mBluetoothAdvertiser == null && (bluetoothAdapter = this.mBluetoothAdapter) != null) {
            this.mBluetoothAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        }
        BluetoothLeAdvertiser bluetoothLeAdvertiser2 = this.mBluetoothAdvertiser;
        Log.e("startAdvertising", "bluetoothLeAdvertiser2 is null? " + (bluetoothLeAdvertiser2 == null));

        if (bluetoothLeAdvertiser2 != null) {
            try {
                bluetoothLeAdvertiser2.startAdvertising(createAdvSettings(true), BleUtil.createIBeaconAdvertiseData(65520, bArr), this.mAdvCallback);
            } catch (Exception unused2) {
                Log.v(TAG, "Fail to setup BleService");
            }
        }
    }

    public AdvertiseSettings createAdvSettings(boolean z) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(2);
        builder.setConnectable(z);
        builder.setTimeout(2500);
        builder.setTxPowerLevel(3);
        AdvertiseSettings build = builder.build();
        if (build == null) {
            Log.e(TAG, "mAdvertiseSettings == null");
        }
        return build;
    }

    public void stopAdvertising() {
        BluetoothGattServer bluetoothGattServer = this.mBluetoothGattServer;
        if (bluetoothGattServer != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothGattServer.close();
        }
        BluetoothLeAdvertiser bluetoothLeAdvertiser = this.mBluetoothAdvertiser;
        if (bluetoothLeAdvertiser != null) {
            try {
                bluetoothLeAdvertiser.stopAdvertising(this.mAdvCallback);
                this.mBluetoothAdvertiser = null;
            } catch (Exception unused) {
            }
        }
    }
}