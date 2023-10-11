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





    //create a broadcast receiver for
    private final BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        print("onReceive: State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        print("on Receive : State Turning OFF");
                        break;

                    case BluetoothAdapter.STATE_ON:
                        print("onReceive : State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        print("onReceive : State Turning on");
                        break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        print("onCreate()");
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        handler = new Handler();

        Button btnOn = findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBluetooth();
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

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            print("enableBluetooth: Device does not support Bluetooth.");
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

            // broadcast the fact that bluetooth changed

            IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothState, newIntent);
        } else{
            print(" Trying to enable but it is already enabled");
        }
    }


    private void disableBluetooth() {
        if (bluetoothAdapter == null) {
            print("enableBluetooth: Device does not support Bluetooth.");
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Disabling bluetooth");
            // check permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestMultiplePermissions();
                return;
            }

            // guide user to disable bluetooth
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);

            // broadcast the fact that bluetooth changed
            IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothState, newIntent);
        }else  {print(" Trying to disable but it is already disabled");}
    }

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
        unregisterReceiver(bluetoothState);
    }
}
