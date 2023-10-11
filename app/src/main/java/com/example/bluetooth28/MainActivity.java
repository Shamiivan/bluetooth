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
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private static final long SCAN_PERIOD = 10000;
    private List<BluetoothDevice> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print("onCreate()");
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler = new Handler();

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
            }
        });

        Button scanBtn = findViewById(R.id.scan);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice();
            }
        });

        Button showBtn = findViewById(R.id.show);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (BluetoothDevice device : deviceList) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        requestMultiplePermissions();
                        return;
                    }
                    Log.i(TAG, "Device: " + device.getName() + " @ " + device.getAddress());
                }
            }
        });

        requestMultiplePermissions();
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Device does not support Bluetooth.");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Enabling Bluetooth");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissions();
                return;
            }
            startActivity(enableIntent);
        }
    }

    private void scanLeDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions();
            return;
        }

        bluetoothLeScanner.startScan(leScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestMultiplePermissions();
                    return;
                }
                bluetoothLeScanner.stopScan(leScanCallback);
            }
        }, SCAN_PERIOD);
    }

    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissions();
                return;
            }
            Log.i(TAG, "Discovered device: " + device.getName() + " @ " + device.getAddress());
            deviceList.add(device);
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions();
            return;
        }
        bluetoothAdapter.cancelDiscovery();
    }

    private void requestMultiplePermissions() {
        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_PERMISSION_CODE);
    }

    public void print(String msg) {
        Log.d(TAG, msg);
    }

    public void display(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

    }

    private void stopScan() {
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissions();
                return;
            }
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    private void displayDevices() {
        for (BluetoothDevice device : deviceList) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissions();
                return;
            }
            Log.i(TAG, "Device: " + device.getName() + " @ " + device.getAddress());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            enableBluetooth();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }
}
