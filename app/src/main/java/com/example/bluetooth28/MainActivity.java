package com.example.bluetooth28;


import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_PERMISSION_CODE = 1;
    private Handler handler;
    private BR_BLUETOOTHSTATE bluetoothState;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.print("onCreate()");
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler();
        bluetoothState = new BR_BLUETOOTHSTATE(getApplicationContext());
        Button btnOn = findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utils.hasBluetooth(bluetoothAdapter)) Utils.enableBluetooth(MainActivity.this,bluetoothAdapter, bluetoothState);
            }
        });

        Button btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableBluetooth();
            }
        });


    }



    private void disableBluetooth() {
        if (bluetoothAdapter == null) {
            Utils.print("enableBluetooth: Device does not support Bluetooth.");
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Disabling bluetooth");
            // check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Utils.requestPermissions(MainActivity.this);
                return;
            }

            // guide user to disable bluetooth
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);

            // broadcast the fact that bluetooth changed
            IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothState, newIntent);
        }else  {Utils.print(" Trying to disable but it is already disabled");}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Utils.requestPermissions(MainActivity.this);
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }






    @Override
    public void onResume() {
        super.onResume();
        if(!Utils.hasBluetooth(bluetoothAdapter)) Utils.enableBluetooth(MainActivity.this,bluetoothAdapter, bluetoothState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothState);
    }
}
