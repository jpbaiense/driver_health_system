package com.jpbaiense.driverhealthapp.data

data class DriveHealthResult(
    val ppgValueON: Int,
    val xAxisData: Int,
    val yAxisData: Int,
    val zAxisData: Int,
    val temperature: Int,
    val pressure: Int,
    val soc: Int,
    val connectionState: ConnectionState
)
