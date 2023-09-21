package com.example.nirjon.bledemo4_advertising.util;

public class BLEUtil {
    private static native void get_rf_payload(byte[] bArr, int i, byte[] bArr2, int i2, byte[] bArr3);

    static {
        System.loadLibrary("ble");
    }

    private static final String BLE_NOTIFY_UUID_A19 = "00002a19-0000-1000-8000-00805f9b34fb";
    private static final String BLE_NOTIFY_UUID_B11 = "0000bb11-0000-1000-8000-00805f9b34fb";
    private static final String BLE_NOTIFY_UUID_B21 = "0000bb21-0000-1000-8000-00805f9b34fb";
    private static final String BLE_SERVICE_UUID_80F = "0000180f-0000-1000-8000-00805f9b34fb";
    private static final String BLE_SERVICE_UUID_AA0 = "0000aaa0-0000-1000-8000-00805f9b34fb";
    private static final String BLE_SERVICE_UUID_B10 = "0000bb10-0000-1000-8000-00805f9b34fb";
    private static final String BLE_SERVICE_UUID_B20 = "0000bb20-0000-1000-8000-00805f9b34fb";
    private static final String BLE_WRITE_UUID_AA1 = "0000aaa1-0000-1000-8000-00805f9b34fb";
    private static final String BLE_WRITE_UUID_AA3 = "0000aaa3-0000-1000-8000-00805f9b34fb";
    public static final String HEX = "0123456789abcdef";
    private static final String rawAddress = "77 62 4d 53 45";

    public static byte[] getBleCommand(String str) {
        return getBleCommand(rawAddress, str);
    }

    public static byte[] getBleCommand(String str, String str2) {
        String lowerCase = str.replace(" ", "").toLowerCase();
        int i = 0;
        if (lowerCase.length() < 6 || lowerCase.length() > 10) {
            return null;
        }
        int length = lowerCase.length() / 2;
        byte[] bArr = new byte[length];
        int i2 = 0;
        while (i2 < length) {
            int i3 = i2 + 1;
            bArr[i2] = strToByte(lowerCase.substring(i2 * 2, i3 * 2));
            i2 = i3;
        }
        String lowerCase2 = str2.replace(" ", "").toLowerCase();
        if (lowerCase2.length() < 2) {
            return null;
        }
        int length2 = lowerCase2.length() / 2;
        byte[] bArr2 = new byte[length2];
        while (i < length2) {
            int i4 = i + 1;
            bArr2[i] = strToByte(lowerCase2.substring(i * 2, i4 * 2));
            i = i4;
        }
        int i5 = length + length2 + 5;
        byte[] bArr3 = new byte[i5];
        get_rf_payload(bArr, length, bArr2, length2, bArr3);
        return bArr3;
    }

    public static byte strToByte(String str) {
        int i;
        if (str.length() == 1) {
            i = HEX.indexOf(str) & 255;
        } else {
            i = charToByte(str.charAt(1)) | (charToByte(str.charAt(0)) << 4);
        }
        return (byte) i;
    }

    public static byte charToByte(char c) {
        return (byte) HEX.indexOf(c);
    }
}
