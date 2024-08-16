package com.jpbaiense.driverhealthapp.data

import com.jpbaiense.driverhealthapp.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface DriveHealthReceiveManager {

    val data: MutableSharedFlow<Resource<DriveHealthResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

    fun read()

}