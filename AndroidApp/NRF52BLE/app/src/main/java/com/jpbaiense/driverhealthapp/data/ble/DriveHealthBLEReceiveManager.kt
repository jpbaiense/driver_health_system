package com.jpbaiense.driverhealthapp.data.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.jpbaiense.driverhealthapp.data.ConnectionState
import com.jpbaiense.driverhealthapp.data.DriveHealthResult
import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager
import com.jpbaiense.driverhealthapp.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
class DriveHealthBLEReceiveManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : DriveHealthReceiveManager {

    private val DEVICE_NAME = "DRIVE_GUARDIAN"
    private val CUSTOM_SERVICE_UIID = "6ab7e5ba-fb0a-08ab-a6ec-7801217ca578"
    private val CUSTOM_CHARACTERISTICS_UUID = "6ab7e5bb-fb0a-08ab-a6ec-7801217ca578"

    override val data: MutableSharedFlow<Resource<DriveHealthResult>> = MutableSharedFlow()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var gatt: BluetoothGatt? = null

    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val scanCallback = object : ScanCallback(){

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if(result.device.name == DEVICE_NAME){
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Connecting to device..."))
                }
                if(isScanning){
                    result.device.connectGatt(context,false, gattCallback)
                    isScanning = false
                    bleScanner.stopScan(this)
                }
            }
        }
    }

    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPTS = 5

    private val gattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(newState == BluetoothProfile.STATE_CONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services..."))
                    }
                    gatt.discoverServices()
                    this@DriveHealthBLEReceiveManager.gatt = gatt
                } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = DriveHealthResult(0,0,0,0,0,0,0,ConnectionState.Disconnected)))
                    }
                    gatt.close()
                }
            }else{
                gatt.close()
                currentConnectionAttempt+=1
                coroutineScope.launch {
                    data.emit(
                        Resource.Loading(
                            message = "Attempting to connect $currentConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"
                        )
                    )
                }
                if(currentConnectionAttempt<=MAXIMUM_CONNECTION_ATTEMPTS){
                    startReceiving()
                }else{
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt){
                printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space..."))
                }
                gatt.requestMtu(517)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            val characteristic = findCharacteristics(CUSTOM_SERVICE_UIID, CUSTOM_CHARACTERISTICS_UUID)
            if(characteristic == null){
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find temp and humidity publisher"))
                }
                return
            }
            enableNotification(characteristic)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            with(characteristic) {
                when(uuid) {
                    UUID.fromString(CUSTOM_CHARACTERISTICS_UUID) -> {
//                        Log.e("BLE_VALUES", "value size: ${value.size}")
                        /*
                             https://chat.openai.com/share/be140a82-4d15-4d4a-b57d-9973ecc5ce55 -> As the struct on the DK side is created with 18 bytes in total:

                             typedef struct __attribute__((packed)) {
                                uint16_t ppg_led_off;   // 2 bytes
                                uint16_t ppg_led_on;    // 2 bytes
                                int16_t x_axis_data;    // 2 bytes
                                int16_t y_axis_data;    // 2 bytes
                                int16_t z_axis_data;    // 2 bytes
                                int32_t temperature;    // 4 bytes
                                int32_t pressure;       // 4 bytes
                            } SensorData;

                            Then, each variable in the app retrieves one SensorData element:

                            val ppgValue_OFF   // 2 bytes
                            val ppgValue_ON  // 2 bytes
                            val xAxisData   // 2 bytes
                            val yAxisData   // 2 bytes
                            val zAxisData   // 2 bytes
                            val temperature  // 4 bytes
                            val pressure    // 4 bytes
                         */

                        val valueReceived: ByteArray = value
                        val buffer = ByteBuffer.wrap(valueReceived)
                        buffer.order(ByteOrder.LITTLE_ENDIAN)
                        val ppgValueON = buffer.short.toInt()
                        val xAxisData = buffer.short.toInt()
                        val yAxisData = buffer.short.toInt()
                        val zAxisData = buffer.short.toInt()
                        val temperature = buffer.int
                        val pressure = buffer.int
                        val socFloat = buffer.float
                        val socInt = (socFloat * 100).toInt()

                        Log.e("BLE_VALUES","ppgValueON: $ppgValueON, xAxisData: $xAxisData, yAxisData: $yAxisData, zAxisData: $zAxisData, temperature: $temperature, pressure: $pressure, socFloat: $socFloat, socInt: $socInt")
                        val driveHealthResult = DriveHealthResult(
                            ppgValueON,
                            xAxisData,
                            yAxisData,
                            zAxisData,
                            temperature,
                            pressure,
                            socInt,
                            ConnectionState.Connected
                        )
                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(data = driveHealthResult)
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun enableNotification(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic, true) == false){
                Log.d("BLEReceiveManager","set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, payload)
        }
    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        gatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }

    private fun findCharacteristics(serviceUUID: String, characteristicsUUID:String):BluetoothGattCharacteristic?{
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    override fun startReceiving() {
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "Scanning Ble devices..."))
        }
        isScanning = true
        bleScanner.startScan(null,scanSettings,scanCallback)
    }

    override fun reconnect() {
        gatt?.connect()
    }

    override fun disconnect() {
        gatt?.disconnect()
    }



    override fun closeConnection() {
        bleScanner.stopScan(scanCallback)
        val characteristic = findCharacteristics(CUSTOM_SERVICE_UIID, CUSTOM_CHARACTERISTICS_UUID)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
        }
        gatt?.close()
    }

    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d("TempHumidReceiveManager","set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

    override fun read() {
        val characteristic = findCharacteristics(this.CUSTOM_SERVICE_UIID,
            this.CUSTOM_CHARACTERISTICS_UUID
        )
        gatt?.let { gatt ->
            gatt.readCharacteristic(characteristic)
        } ?: run {
        }
    }

}