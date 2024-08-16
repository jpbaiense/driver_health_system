package com.jpbaiense.driverhealthapp.presentation

import android.bluetooth.BluetoothAdapter
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jpbaiense.driverhealthapp.data.ConnectionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jpbaiense.driverhealthapp.data.ble.rememberMarker
import com.jpbaiense.driverhealthapp.presentation.permissions.PermissionUtils
import com.jpbaiense.driverhealthapp.presentation.permissions.SystemBroadcastReceiver
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.layout.fullWidth
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.chart.zoom.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TemperatureHumidityScreen(
    onBluetoothStateChanged:()->Unit,
    viewModel: DriveHealthViewModel = hiltViewModel()
) {

    var onValuesList by remember { mutableStateOf(mutableListOf(0)) } // Initialize with 0]
    var onValue: Int = 0

    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED){ bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
            onBluetoothStateChanged()
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{_,event ->
                if(event == Lifecycle.Event.ON_START){
                    permissionState.launchMultiplePermissionRequest()
                    if(permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected){
                        viewModel.reconnect()
                    }
                }
                if(event == Lifecycle.Event.ON_STOP){
                    if (bleConnectionState == ConnectionState.Connected){
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(key1 = permissionState.allPermissionsGranted){
        if(permissionState.allPermissionsGranted){
            if(bleConnectionState == ConnectionState.Uninitialized){
                viewModel.initializeConnection()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Occupy available space
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .aspectRatio(1f)
                    .border(
                        BorderStroke(
                            5.dp, Color.Blue
                        ),
                        RoundedCornerShape(10.dp)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                if(bleConnectionState == ConnectionState.CurrentlyInitializing){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        CircularProgressIndicator()
                        if(viewModel.initializingMessage != null){
                            Text(
                                text = viewModel.initializingMessage!!
                            )
                        }
                    }
                }else if(!permissionState.allPermissionsGranted){
                    Text(
                        text = "Go to the app setting and allow the missing permissions.",
//                    style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }else if(viewModel.errorMessage != null){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.errorMessage!!
                        )
                        Button(
                            onClick = {
                                if(permissionState.allPermissionsGranted){
                                    viewModel.initializeConnection()
                                }
                            }
                        ) {
                            Text(
                                "Try again"
                            )
                        }
                    }
                }else if(bleConnectionState == ConnectionState.Connected){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){

                        Text(
                            text = "PPG signal: ${viewModel.ppg}"
//                        style = MaterialTheme.typography
                        )
                    }
                }else if(bleConnectionState == ConnectionState.Disconnected){
                    Button(onClick = {
                        viewModel.initializeConnection()
                    }) {
                        Text("Initialize again")
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f), // Occupy available space
            contentAlignment = Alignment.Center
        ) {
            try {
                onValue = viewModel.ppg.toInt()
                onValuesList.add(viewModel.ppg.toInt())
                Log.e("VALUES_CHANGED","${onValuesList.size}")

                if (onValuesList.size > 20) {
                    onValuesList = onValuesList.subList(onValuesList.size - 20, onValuesList.size).toMutableList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val modelProducer = remember { CartesianChartModelProducer.build() }

            LaunchedEffect(onValue) {
                modelProducer.tryRunTransaction {
                    lineSeries {
                        Log.e("VALUES_CHANGED","INSIDE ${onValuesList.size}")
                        series(y = onValuesList) // Pass the list of ON values to the series function
                    }
                }
            }

            val lineBlueColor = Color(0xFF1674E7)

            val bottomAxisLabelBackgroundColor = Color(0xFF575C54)
            val axisValueOverrider = AxisValueOverrider.adaptiveYValues(yFraction = 1.2f, round = true)

            CartesianChartHost(
                chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lines = listOf(rememberLineSpec(shader = DynamicShaders.color(lineBlueColor))),
                        axisValueOverrider = axisValueOverrider,
                    ),
                    startAxis =
                    rememberStartAxis(
                        label = rememberAxisLabelComponent(
                            color = Color.Black,
                            padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                            margins = dimensionsOf(all = 4.dp),
                        ),
                        guideline = null,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                        titleComponent =
                        rememberTextComponent(
                            color = Color.White,
                            background = rememberShapeComponent(Shapes.pillShape, lineBlueColor),
                            padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                            margins = dimensionsOf(end = 4.dp),
                            typeface = Typeface.MONOSPACE,
                        ),
                        title = "Heart rate data",
                    ),
                    bottomAxis =
                    rememberBottomAxis(
                        label = rememberAxisLabelComponent(
                            color = Color.Black,
                            padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                            margins = dimensionsOf(all = 4.dp),
                        ),
                        titleComponent =
                        rememberTextComponent(
                            background = rememberShapeComponent(Shapes.pillShape, bottomAxisLabelBackgroundColor),
                            color = Color.White,
                            padding = dimensionsOf(horizontal = 8.dp, vertical = 2.dp),
                            margins = dimensionsOf(top = 4.dp),
                            typeface = Typeface.MONOSPACE,
                        ),
                        title = "Time (ms)",
                    ),
                    fadingEdges = rememberFadingEdges(),
                ),
                modelProducer = modelProducer,
                modifier = Modifier,
                marker = rememberMarker(MarkerComponent.LabelPosition.AroundPoint),
                runInitialAnimation = false,
                horizontalLayout = HorizontalLayout.fullWidth(),
                zoomState = rememberVicoZoomState(zoomEnabled = true),
            )
        }
    }
}











