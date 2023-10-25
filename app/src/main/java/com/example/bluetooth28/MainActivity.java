package com.example.bluetooth28;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner btScanner;
    private static final int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_PERMISSION_CODE = 1;
    private Handler handler;
    private BR_BLUETOOTH_STATE bluetoothState;
    private static final long SCAN_PERIOD = 10000;
    ArrayList<BLE_Device> devices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.print("onCreate()");
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        btScanner = bluetoothAdapter.getBluetoothLeScanner();

        handler = new Handler();
        bluetoothState = new BR_BLUETOOTH_STATE(getApplicationContext());
        devices = new ArrayList<>();
        Button btnOn = findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.hasBluetooth(bluetoothAdapter))
                    Utils.enableBluetooth(MainActivity.this, bluetoothState);
            }
        });

        Button btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasBluetooth(bluetoothAdapter))
                    Utils.disableBluetooth(MainActivity.this, bluetoothState);
            }
        });
        Button btnScan = findViewById(R.id.scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.hasBluetooth(bluetoothAdapter))
                    Utils.enableBluetooth(MainActivity.this, bluetoothState);
                startScanning();
            }
        });


        Button stopScan = findViewById(R.id.stopScan);
        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScanning();
                if (devices.size() > 0) {
                    for (BLE_Device device : devices) {
                        String deviceInfo = "Device Name:  " + device.getName() + "  Address : " + device.getAddress() + " rssi : " + device.getRSSI() + "\n";
                        String espName = "ESP32";
                        Utils.print(deviceInfo);
                    }
                } else Utils.print("No devices found");
            }
        });

        //request permission
        Utils.requestPermissions(MainActivity.this);


    }

    public void startScanning() {
        Utils.print("Scanning started");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Utils.requestPermissions(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        Utils.print("Scan Stopping");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Utils.requestPermissions(MainActivity.this);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Utils.requestPermissions(MainActivity.this);
                return;
            }
            BLE_Device device = new BLE_Device(result.getDevice(), result.getDevice().getName(), result.getRssi());
                devices.add(device);
           }
       };


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
        if(!Utils.hasBluetooth(bluetoothAdapter)) Utils.enableBluetooth(MainActivity.this,bluetoothState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothState);
    }
}
