### Project: **BLE Device Scanner with ESP32**

#### **Description**:
This project showcases an Android-based Bluetooth Low Energy (BLE) scanner application that interacts with ESP32 devices. The app scans for nearby BLE devices, retrieves their details, and enables notifications from the ESP32 BLE server. The ESP32 server broadcasts periodic updates to connected clients, demonstrating a complete BLE communication setup.

---

#### **Features**:

- **Android BLE Scanner**:
  - Scan for BLE devices using the `BluetoothLeScanner` API.
  - Display scanned device information: Name, Address, and RSSI (signal strength).
  - Buttons to enable/disable Bluetooth and start/stop scanning.
  - Permissions handling for Android's BLE access.

- **ESP32 BLE Server**:
  - Implements a BLE server using the Arduino framework.
  - Advertises a BLE service with a custom UUID.
  - Sends periodic notifications to connected clients.
  - Supports characteristic properties: Read, Write, Notify, and Indicate.

- **Seamless Communication**:
  - Android app receives and processes data from the ESP32 server.
  - Handles Bluetooth state changes dynamically with broadcast receivers.

---

#### **Key Components**:

1. **Android App**:
   - **MainActivity**: Manages BLE operations, user interface, and device scanning.
   - **Utils**: Provides utility functions for Bluetooth enablement, permission handling, and logging.
   - **BR_BLUETOOTH_STATE**: Broadcast receiver for monitoring Bluetooth state changes.
   - **BLE_Device**: Represents a BLE device with details like name, address, and signal strength.

2. **ESP32 BLE Server**:
   - Uses the `BLEDevice`, `BLEServer`, and `BLECharacteristic` classes for server implementation.
   - Periodically notifies connected devices with updated characteristic values.
   - Custom UUIDs for service and characteristics to identify the BLE server.

---

#### **How It Works**:

1. **Setup ESP32 Server**:
   - Flash the provided Arduino sketch to the ESP32.
   - The ESP32 starts advertising a BLE service with periodic notifications.

2. **Run Android App**:
   - Install the app on an Android device.
   - Enable Bluetooth and scan for nearby BLE devices.
   - Stop the scan to display the scanned device details.
   - Interact with the ESP32 BLE server.

---

#### **Technologies Used**:
- **Android**: Java, Android SDK (Bluetooth APIs).
- **ESP32**: Arduino framework, BLE libraries.
- **UUID Generator**: Custom UUIDs for BLE services and characteristics.

---

#### **Getting Started**:

1. **Android App**:
   - Clone the repository:
     ```bash
     git clone <repository-url>
     ```
   - Open the project in Android Studio.
   - Build and run the app on an Android device with BLE support.

2. **ESP32**:
   - Install the Arduino IDE and ESP32 libraries.
   - Flash the `ESP32 BLE Server` code to your ESP32 device.

---

#### **Future Enhancements**:
- Add a user interface for visualizing notifications from the ESP32 server.
- Implement data logging for scanned devices and received notifications.
- Extend BLE server functionality with additional characteristics and services.
---
