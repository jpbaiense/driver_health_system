package com.jpbaiense.driverhealthapp.data.ble;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;
import com.jpbaiense.driverhealthapp.data.ConnectionState;
import com.jpbaiense.driverhealthapp.data.DriveHealthResult;
import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager;
import com.jpbaiense.driverhealthapp.util.Resource;
import kotlinx.coroutines.Dispatchers;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import javax.inject.Inject;

@android.annotation.SuppressLint(value = {"MissingPermission"})
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\'\u001a\u00020(H\u0016J\b\u0010)\u001a\u00020(H\u0016J\u0010\u0010*\u001a\u00020(2\u0006\u0010+\u001a\u00020,H\u0002J\u0010\u0010-\u001a\u00020(2\u0006\u0010+\u001a\u00020,H\u0002J\u001a\u0010.\u001a\u0004\u0018\u00010,2\u0006\u0010/\u001a\u00020\b2\u0006\u00100\u001a\u00020\bH\u0002J\b\u00101\u001a\u00020(H\u0016J\b\u00102\u001a\u00020(H\u0016J\b\u00103\u001a\u00020(H\u0016J\u0018\u00104\u001a\u00020(2\u0006\u00105\u001a\u0002062\u0006\u00107\u001a\u000208H\u0002R\u000e\u0010\u0007\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R#\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\u0013\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R \u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00190\u0018X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020$X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010%\u001a\n \u000f*\u0004\u0018\u00010&0&X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ble/DriveHealthBLEReceiveManager;", "Lcom/jpbaiense/driverhealthapp/data/DriveHealthReceiveManager;", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "context", "Landroid/content/Context;", "(Landroid/bluetooth/BluetoothAdapter;Landroid/content/Context;)V", "CUSTOM_CHARACTERISTICS_UUID", "", "CUSTOM_SERVICE_UIID", "DEVICE_NAME", "MAXIMUM_CONNECTION_ATTEMPTS", "", "bleScanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "kotlin.jvm.PlatformType", "getBleScanner", "()Landroid/bluetooth/le/BluetoothLeScanner;", "bleScanner$delegate", "Lkotlin/Lazy;", "coroutineScope", "Lkotlinx/coroutines/CoroutineScope;", "currentConnectionAttempt", "data", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/jpbaiense/driverhealthapp/util/Resource;", "Lcom/jpbaiense/driverhealthapp/data/DriveHealthResult;", "getData", "()Lkotlinx/coroutines/flow/MutableSharedFlow;", "gatt", "Landroid/bluetooth/BluetoothGatt;", "gattCallback", "Landroid/bluetooth/BluetoothGattCallback;", "isScanning", "", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "scanSettings", "Landroid/bluetooth/le/ScanSettings;", "closeConnection", "", "disconnect", "disconnectCharacteristic", "characteristic", "Landroid/bluetooth/BluetoothGattCharacteristic;", "enableNotification", "findCharacteristics", "serviceUUID", "characteristicsUUID", "read", "reconnect", "startReceiving", "writeDescription", "descriptor", "Landroid/bluetooth/BluetoothGattDescriptor;", "payload", "", "app_debug"})
public final class DriveHealthBLEReceiveManager implements com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager {
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    private final android.content.Context context = null;
    private final java.lang.String DEVICE_NAME = "DRIVE_GUARDIAN";
    private final java.lang.String CUSTOM_SERVICE_UIID = "6ab7e5ba-fb0a-08ab-a6ec-7801217ca578";
    private final java.lang.String CUSTOM_CHARACTERISTICS_UUID = "6ab7e5bb-fb0a-08ab-a6ec-7801217ca578";
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.jpbaiense.driverhealthapp.util.Resource<com.jpbaiense.driverhealthapp.data.DriveHealthResult>> data = null;
    private final kotlin.Lazy bleScanner$delegate = null;
    private final android.bluetooth.le.ScanSettings scanSettings = null;
    private android.bluetooth.BluetoothGatt gatt;
    private boolean isScanning = false;
    private final kotlinx.coroutines.CoroutineScope coroutineScope = null;
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    private int currentConnectionAttempt = 1;
    private int MAXIMUM_CONNECTION_ATTEMPTS = 5;
    private final android.bluetooth.BluetoothGattCallback gattCallback = null;
    
    @javax.inject.Inject
    public DriveHealthBLEReceiveManager(@org.jetbrains.annotations.NotNull
    android.bluetooth.BluetoothAdapter bluetoothAdapter, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public kotlinx.coroutines.flow.MutableSharedFlow<com.jpbaiense.driverhealthapp.util.Resource<com.jpbaiense.driverhealthapp.data.DriveHealthResult>> getData() {
        return null;
    }
    
    private final android.bluetooth.le.BluetoothLeScanner getBleScanner() {
        return null;
    }
    
    private final void enableNotification(android.bluetooth.BluetoothGattCharacteristic characteristic) {
    }
    
    private final void writeDescription(android.bluetooth.BluetoothGattDescriptor descriptor, byte[] payload) {
    }
    
    private final android.bluetooth.BluetoothGattCharacteristic findCharacteristics(java.lang.String serviceUUID, java.lang.String characteristicsUUID) {
        return null;
    }
    
    @java.lang.Override
    public void startReceiving() {
    }
    
    @java.lang.Override
    public void reconnect() {
    }
    
    @java.lang.Override
    public void disconnect() {
    }
    
    @java.lang.Override
    public void closeConnection() {
    }
    
    private final void disconnectCharacteristic(android.bluetooth.BluetoothGattCharacteristic characteristic) {
    }
    
    @java.lang.Override
    public void read() {
    }
}