package com.jpbaiense.driverhealthapp.presentation

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.jpbaiense.driverhealthapp.data.ConnectionState
import com.jpbaiense.driverhealthapp.data.ble.rememberMarker
import com.jpbaiense.driverhealthapp.ui.theme.DarkBlue
import com.jpbaiense.driverhealthapp.ui.theme.Cream
import com.jpbaiense.driverhealthapp.ui.theme.LightBlue
import com.jpbaiense.driverhealthapp.ui.theme.LightGreen
import com.jpbaiense.driverhealthapp.ui.theme.LightPink
import com.jpbaiense.driverhealthapp.ui.theme.LightRed
import com.jpbaiense.driverhealthapp.ui.theme.LightYellow
import com.jpbaiense.driverhealthapp.ui.theme.DarkGreen
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
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.Typeface
import com.jpbaiense.driverhealthapp.R
import com.jpbaiense.driverhealthapp.presentation.permissions.PermissionUtils
import com.jpbaiense.driverhealthapp.presentation.permissions.SystemBroadcastReceiver
import com.jpbaiense.driverhealthapp.ui.theme.LightGrey
import com.jpbaiense.driverhealthapp.ui.theme.MediumBlue
import com.jpbaiense.driverhealthapp.ui.theme.MediumGreen
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen( // TODO: Move out the processing steps from the screen file
    onBluetoothStateChanged:()->Unit,
    context: Context,
    viewModel: DriveHealthViewModel = hiltViewModel()
) {
    var isDataReadyToModel = false
    Log.e("HOW_MANY","How many times this is called $isDataReadyToModel")
    val ppgValueList = remember { mutableStateListOf(0) }
    var xAxisValueList = remember { mutableStateListOf(0) }
    var yAxisValueList = remember { mutableStateListOf(0) }
    var zAxisValueList = remember { mutableStateListOf(0) }

    var temperatureValueList = remember { mutableStateListOf(0) }
    temperatureValueList.add(viewModel.temperature)

    var pressureValueList = remember { mutableStateListOf(0) }
    pressureValueList.add(viewModel.pressure)

    var socValueList = remember { mutableStateListOf(0) }
    socValueList.add(viewModel.soc)

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

    val modelInputData = remember { mutableStateOf(emptyArray<Array<Float>>()) }
    val modelInputList = remember { mutableStateListOf(0) }

    LaunchedEffect(viewModel) {
        while (true) {
            try {
                if (ppgValueList.size >= 270) {
                    ppgValueList.removeAt(0) // Remove the oldest element
                }
                ppgValueList.add(viewModel.ppg)

                Log.e("model_input","modelInputList size: ${modelInputList.size}")
                if (modelInputList.size >= 265) {
                    isDataReadyToModel = true
                    Log.e("HOW_MANY","How many times this is called INSIDE LOG $isDataReadyToModel")
                    modelInputList.removeAt(0) // Remove the oldest element
                    val mean = modelInputList.average()
                    val stdDev = kotlin.math.sqrt(modelInputList.map { (it - mean) * (it - mean) }.average())

//                    val dataArray = Array(1) { Array(10) { 0.0F } }
//                    for (i in 0 until 10) {
//                        // Apply z normalization to each value
//                        dataArray[0][i] = ((modelInputList[i] - mean) / stdDev).toFloat()
//                        Log.e("model_input", "dataArray[0][i]: ${dataArray[0][i]}, index: $i")
//                    }

                    val dataArray = Array(1) { Array(264) { 0.0F } }
                    for (i in 0 until 264) {
                        dataArray[0][i] = ((ppgValueList[i] - mean) / stdDev).toFloat()
                    }

                    val arraySize = dataArray[0].size
                    Log.e("model_input", "Data array size: $arraySize")
                    modelInputData.value = dataArray

                    for ((index, array) in modelInputData.value.withIndex()) {
                        val innerArraySize = array.size
                        Log.e("model_input", "Inner array $index size: $innerArraySize")
                    }
//                    Log.e("model_input","Data array: ${dataArray.contentDeepToString()}")
//                    Log.e("model_input","Data array SIZEEE: ${modelInputData.value.size}")
//                    Log.e("model_input","isDataReadyToModel: ${isDataReadyToModel}")
                }
                modelInputList.add(viewModel.ppg)

                if (xAxisValueList.size >= 270) {
                    xAxisValueList.removeAt(0) // Remove the oldest element
                }
                xAxisValueList.add(viewModel.xAxis)

                if (yAxisValueList.size >= 270) {
                    yAxisValueList.removeAt(0) // Remove the oldest element
                }
                yAxisValueList.add(viewModel.yAxis)

                if (zAxisValueList.size >= 270) {
                    zAxisValueList.removeAt(0) // Remove the oldest element
                }
                zAxisValueList.add(viewModel.zAxis)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(500L)
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Cream)){
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            GreetingSection()
            DiagnosticsSection(ppgValueList, xAxisValueList, yAxisValueList, zAxisValueList)
            HealthDataBatterySection(temperatureValueList, pressureValueList, modelInputData.value, isDataReadyToModel, context)
            BatteryLevel(socValueList)
        }
    }
}

fun ModelEstimate(
    modelInputData: Array<Array<Float>>,
    outputText: MutableState<String?>
) {
    var outputValue: Float = 0F

    Firebase.analytics.logEvent("log_button_clicked",null)

    Log.e("model_input","Data array inside MODEL ESTIMATE: ${modelInputData.contentDeepToString()}")

    Log.e("LOG_MODEL","Inside ModelEstimate function")
    val conditions = CustomModelDownloadConditions.Builder()
        .requireWifi()
        .build()
    FirebaseModelDownloader.getInstance()
        .getModel("PPG-model", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
            conditions)
        .addOnSuccessListener { model: CustomModel? ->
            Log.e("LOG_MODEL","addOnSuccessListener")
            // Download complete

            // The CustomModel object contains the local path of the model file,
            // which you can use to instantiate a TensorFlow Lite interpreter.
            val modelFile = model?.file
            if (modelFile != null) {
                Log.e("LOG_MODEL","modelFile != null")
                val interpreter = Interpreter(modelFile)

                Log.e("LOG_MODEL","Interpreter")

                val flattenedInputData = modelInputData.flatten() // 1D array

                val inputBuffer = ByteBuffer.allocateDirect(flattenedInputData.size * java.lang.Float.SIZE / java.lang.Byte.SIZE)
                    .order(ByteOrder.nativeOrder())


                for (element in flattenedInputData) {
                    inputBuffer.putFloat(element) // set it as float
                }

                inputBuffer.flip() // reset buffer position

                val bufferSize = 4 // output is a single float value (4 bytes)
                val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

                interpreter.run(inputBuffer, modelOutput) // perform inference

                modelOutput.rewind()
                outputValue = modelOutput.float

                Log.e("LOG_MODEL", "Model output: $outputValue")
                outputText.value = String.format("%.2f", outputValue)
            }
        }
        .addOnFailureListener { e ->
            Log.e("LOG_MODEL", "Model download failed", e)
        }
    Log.e("LOG_MODEL","It worked")
}

@Composable
fun GreetingSection() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ){

        Icon(
            painter = painterResource(id = R.drawable.account_profile_circle),
            contentDescription = "Account_profile",
            modifier = Modifier.size(50.dp)
        )

        Column (
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Hello, João!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
        }
    }
}

@Composable
fun DiagnosticsSection(
    ppgValueList: SnapshotStateList<Int>,
    xAxisValueList: SnapshotStateList<Int>,
    yAxisValueList: SnapshotStateList<Int>,
    zAxisValueList: SnapshotStateList<Int>
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(LightBlue)
                .height(330.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Your personal data",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(5.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    val modelProducer = remember { CartesianChartModelProducer.build() }

                    LaunchedEffect(xAxisValueList, yAxisValueList, zAxisValueList, ppgValueList) {
                        withContext(Dispatchers.Default) {
                            while (true) {
                                modelProducer.tryRunTransaction {
                                    lineSeries {
                                        series(xAxisValueList.toList())
                                        series(yAxisValueList.toList())
                                        series(zAxisValueList.toList())
                                    }
                                    lineSeries {
                                        series(ppgValueList.toList())
                                    }
                                }
                                delay(500L)
                            }
                        }
                    }

                    Log.e(
                        "Test_values",
                        "ppg list: ${ppgValueList}, x list: ${xAxisValueList}, y list: ${yAxisValueList}, z list: ${zAxisValueList}"
                    )

                    val bottomAxisLabelBackgroundColor = Color(0xFF575C54)
                    val axisValueOverrider =
                        AxisValueOverrider.adaptiveYValues(yFraction = 1.2f, round = true)

                    val ppgMaxValue = (ppgValueList.last() + 1000).toFloat()
                    val ppgMinValue = (ppgValueList.last() - 1000).toFloat()

                    Log.e("PPG_MIN_MAX", "ppgMinValue: ${ppgMinValue}, ppgMaxValue: ${ppgMaxValue}")


                    val chartColorsACC =
                        listOf(Color(0xFF26C8FF), Color(0xFFFF0B0B), Color(0xFFFFA602))

                    val chartColorPPG = listOf(Color(0xFF0000FF))

                    CartesianChartHost(
                        chart =
                        rememberCartesianChart(
                            rememberLineCartesianLayer(
                                lines =
                                chartColorsACC.map { color ->
                                    rememberLineSpec(
                                        shader = DynamicShaders.color(color),
                                        backgroundShader = null,
                                    )
                                },
                                verticalAxisPosition = AxisPosition.Vertical.Start
                            ),
                            rememberLineCartesianLayer(
                                lines =
                                chartColorPPG.map { color ->
                                    rememberLineSpec(
                                        shader = DynamicShaders.color(color),
                                        backgroundShader = null,
                                    )
                                },
                                verticalAxisPosition = AxisPosition.Vertical.End,
                                axisValueOverrider = AxisValueOverrider.fixed(
                                    minY = ppgMinValue.toFloat(),
                                    maxY = ppgMaxValue.toFloat()
                                )
                            ),
                            startAxis =
                            rememberStartAxis(
                                label = rememberAxisLabelComponent(
                                    color = Color.Black,
                                    padding = dimensionsOf(horizontal = 1.dp, vertical = 2.dp),
                                    margins = dimensionsOf(all = 2.dp),
                                    textSize = 6.em
                                ),
                                titleComponent =
                                rememberTextComponent(
                                    color = Color.White,
                                    background = rememberShapeComponent(RoundedCornerShape(3.dp), MediumGreen),
                                    padding = dimensionsOf(horizontal = 1.dp, vertical = 1.dp),
                                    margins = dimensionsOf(end = 2.dp),
                                    typeface = android.graphics.Typeface.MONOSPACE,
                                ),
                                guideline = null,
                                title = "Acceleromter signal",
                                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside
                            ),
                            bottomAxis =
                            rememberBottomAxis(
                                label = rememberAxisLabelComponent(
                                    color = Color.Black,
                                    padding = dimensionsOf(horizontal = 1.dp, vertical = 1.dp),
                                    margins = dimensionsOf(all = 1.dp),
                                    textSize = 10.em
                                ),
                                titleComponent =
                                rememberTextComponent(
                                    color = Color.Black,
                                    textSize = 14.em
                                ),
                                title = "Time (ms)",
                            ),
                            endAxis =
                            rememberEndAxis(
                                label = rememberAxisLabelComponent(
                                    color = Color.Black,
                                    padding = dimensionsOf(horizontal = 1.dp, vertical = 2.dp),
                                    margins = dimensionsOf(all = 2.dp),
                                    textSize = 6.em
                                ),
                                titleComponent =
                                rememberTextComponent(
                                    color = Color.White,
                                    background = rememberShapeComponent(RoundedCornerShape(3.dp), MediumBlue),
                                    padding = dimensionsOf(horizontal = 1.dp, vertical = 1.dp),
                                    margins = dimensionsOf(end = 2.dp),
                                    typeface = android.graphics.Typeface.MONOSPACE,
                                ),
                                guideline = null,
                                title = "Heart rate signal",
                                horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside
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

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {

                    Canvas(modifier = Modifier.size(5.dp), onDraw = {
                        drawCircle(color = Color(0xFF0000FF))
                    })

                    Spacer(modifier = Modifier.padding(3.dp))

                    Text(
                        text = "Heart rate",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Canvas(modifier = Modifier.size(5.dp), onDraw = {
                        drawCircle(color = Color(0xFF26C8FF))
                    })

                    Spacer(modifier = Modifier.padding(3.dp))

                    Text(
                        text = "X-Axis",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Canvas(modifier = Modifier.size(5.dp), onDraw = {
                        drawCircle(color = Color(0xFFFF0B0B))
                    })

                    Spacer(modifier = Modifier.padding(3.dp))

                    Text(
                        text = "Y-Axis",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Canvas(modifier = Modifier.size(5.dp), onDraw = {
                        drawCircle(color = Color(0xFFFFA602))
                    })

                    Spacer(modifier = Modifier.padding(3.dp))

                    Text(
                        text = "Z-Axis",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HealthDataBatterySection(temperature: MutableList<Int>, pressure: MutableList<Int>, modelInputData: Array<Array<Float>>, isDataReadyToModel : Boolean, context: Context) {
    var outputText = remember { mutableStateOf<String?>(null) }
    val showText = remember { mutableStateOf(false) }
    Row (horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()) {
        Column (verticalArrangement = Arrangement.spacedBy(10.dp)){
            Log.e("HOW_MANY","How many times this is called $isDataReadyToModel")
            Column (verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightPink)
                    .padding(10.dp)
                    .width(150.dp)
                    .height(80.dp)) {
                Row (horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_temperature),
                        contentDescription = "HeartRateIcon",
                        modifier = Modifier
                            .size(40.dp)
                    )
                    Text(text = "Temperature",
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 5.dp))
                }
                Text(text = "${temperature.last().toString()} Cº",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
            }

            Column (verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightRed)
                    .padding(10.dp)
                    .width(150.dp)
                    .height(80.dp)) {
                Row (horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_air_wave),
                        contentDescription = "AirWaveIcon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(top = 5.dp)
                    )
                    Text(text = "Barometric Pressure",
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center)
                }
                Text(text = "${pressure.last().toString()} Pa",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(LightYellow)
                .padding(10.dp)
                .height(190.dp)) {
            Button(onClick = {
                if (modelInputData.isNullOrEmpty()){
                    Log.e("HOW_MODEL","data not ready yet")
                    Log.e("OUTPUT_TEXT","data not ready yet")
                    Toast.makeText(context, "Model input data is not ready yet", Toast.LENGTH_SHORT).show()
                } else {
                    for ((index, array) in modelInputData.withIndex()) {
                        val innerArraySize = array.size
                        if (innerArraySize >= 10) {
                            ModelEstimate(modelInputData, outputText)
                            Log.e("OUTPUT_TEXT","outputText ${outputText.value}")
                            showText.value = true
                        }
                    }
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cream
                ),
                modifier = Modifier
                    .padding(5.dp)) {
                Text(text = "Estimate heart rate value with AI",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold)
            }

            LaunchedEffect(showText.value) {
                if (showText.value) {
                    delay(10000)
                    showText.value = false
                }
            }

            AnimatedVisibility(
                visible = showText.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (outputText.value != null) {
                    Log.e("OUTPUT_TEXT", "IT IS NOT NULL ${outputText.value}")
                    Text(
                        text = "The estimated heart rate is ${outputText.value} bpm",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!showText.value) {
                Log.e("OUTPUT_TEXT", "IT IS NULL")
                Icon(
                    painter = painterResource(id = R.drawable.ic_ai_model),
                    contentDescription = "AIModelIcon",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BatteryLevel(soc: MutableList<Int>) {
    val batteryLevel = soc.lastOrNull()?.toFloat()?.div(100) ?: 0f
    Row (horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 15.dp, end = 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LightGrey)
            .fillMaxWidth()) {

        CustomLinearProgressIndicator(
            progress = batteryLevel,
            progressColor = LightGreen
        )

        Column (verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 25.dp, bottom = 20.dp, end = 20.dp)) {
            Text(text = "Battery",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center)

            Text(text = "${soc.last()} %",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.Green,
    backgroundColor: Color = Color.Blue,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = Modifier
            .padding(top = 25.dp, bottom = 20.dp, start = 15.dp)
            .clip(RoundedCornerShape(50.dp))
            .width(200.dp)
            .height(70.dp)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
        )
    }
}