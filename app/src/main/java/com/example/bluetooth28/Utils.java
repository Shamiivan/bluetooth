package com.example.bluetooth28;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class Utils {
   private static final String TAG = "MAIN_ACTIVITY";
    public static boolean hasBluetooth(BluetoothAdapter adapter){
        if(adapter == null || !(adapter.isEnabled())) return false;
        return true;
    }

    public static void enableBluetooth(Activity activity, BR_BLUETOOTH_STATE bluetoothState) {

            Log.d(TAG, "Enabling Bluetooth");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Utils.requestPermissions(activity);
                return;
            }
            activity.startActivity(enableIntent);

            // broadcast the fact that bluetooth changed
            IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            activity.registerReceiver(bluetoothState, newIntent);
        };

    public static void disableBluetooth(Activity activity, BR_BLUETOOTH_STATE bluetoothState) {
            // guide user to disable bluetooth
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            activity.startActivity(intent);

            // broadcast the fact that bluetooth changed
            IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            activity.registerReceiver(bluetoothState, newIntent);
        }

    public static void requestPermissions(Activity activity) {
        int BLUETOOTH_PERMISSION_CODE = 1;
        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, BLUETOOTH_PERMISSION_CODE);
    }

    public static void print(String msg) {
        Log.d(TAG, msg);
    }
    public static void display(Context context,  String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

};


