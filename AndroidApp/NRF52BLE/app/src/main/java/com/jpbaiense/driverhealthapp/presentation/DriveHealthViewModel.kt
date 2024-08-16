package com.jpbaiense.driverhealthapp.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpbaiense.driverhealthapp.data.ConnectionState
import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager
import com.jpbaiense.driverhealthapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriveHealthViewModel @Inject constructor(
    private val driveHealthReceiveManager: DriveHealthReceiveManager
) : ViewModel(){

    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var ppg by mutableStateOf(0)
        private set

    var xAxis by mutableStateOf(0)
        private set

    var yAxis by mutableStateOf(0)
        private set

    var zAxis by mutableStateOf(0)
        private set

    var temperature by mutableStateOf(0)
        private set

    var pressure by mutableStateOf(0)
        private set

    var soc by mutableStateOf(0)
        private set

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges(){
        viewModelScope.launch {
            driveHealthReceiveManager.data.collect{ result ->
                when(result){
                    is Resource.Success -> {
                        connectionState = result.data.connectionState
                        ppg = result.data.ppgValueON
                        xAxis = result.data.xAxisData
                        yAxis = result.data.yAxisData
                        zAxis = result.data.zAxisData
                        temperature = result.data.temperature
                        pressure = result.data.pressure
                        soc = result.data.soc
                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }
                }
            }
        }
    }

    fun disconnect(){
        driveHealthReceiveManager.disconnect()
    }

    fun reconnect(){
        driveHealthReceiveManager.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        driveHealthReceiveManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        driveHealthReceiveManager.closeConnection()
    }

    fun read(){
        driveHealthReceiveManager.read()
    }

}