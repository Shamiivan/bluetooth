package com.example.bluetooth28;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_PERMISSION_CODE = 1;
    private boolean scanning;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enableDisableBT: Device does not support Bluetooth.");
            return;
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            print("Permission not there yet already ");
            Toast.makeText(MainActivity.this, "NEED PERMISSION ", Toast.LENGTH_SHORT).show();
            // request to activate Bluetooth from the user
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_CODE);
            return;
        } else {
            display("Permission already GRANTED");

        }


        if (!bluetoothAdapter.isEnabled()) {
            print("Enabling Bluetooth");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
        }
    }

    private void requestBluetoothPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Need")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                            {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                                    BLUETOOTH_PERMISSION_CODE);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                    BLUETOOTH_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanLeDevice() {
        if (!scanning) {
            //stops scanning after a predefined scan period
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_PERMISSION_CODE);
                        return;
                    }
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        }else{
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    };

//        private LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
//            leDeviceListAdapter.addDevice(result.getDevice());
//            leDeviceListAdapter.notifyDataSetChanged();
            String res = String.valueOf(result.getDevice());
            print(res);
        }
    };

    public void print(String msg){
        Log.d(TAG, msg);
    }
    public void display(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}
