package com.example.bluetooth28;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class BLE_Device {
    private BluetoothDevice device;
    private int rssi;
    private String name;

    public BLE_Device(BluetoothDevice device) {
        this.device = device;
    }

    public BLE_Device(BluetoothDevice _device, String name, int rssi) {
        this.device = _device;
        this.rssi = rssi;
        this.name = name;
    }

    public String getAddress(){
        return device.getAddress();
    }
    public String getName(){
        return  name;
    }
    public void setRSSI(int _rssi){
        this.rssi = rssi;
    }
    public int getRSSI(){
        return rssi;
    }
}
