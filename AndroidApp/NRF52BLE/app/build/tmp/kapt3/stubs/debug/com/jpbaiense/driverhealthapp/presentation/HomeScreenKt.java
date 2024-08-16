package com.jpbaiense.driverhealthapp.presentation;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.CheckboxColors;
import androidx.compose.material3.Shapes;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.snapshots.SnapshotStateList;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import com.google.accompanist.permissions.ExperimentalPermissionsApi;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.jpbaiense.driverhealthapp.data.ConnectionState;
import com.patrykandpatrick.vico.core.axis.AxisPosition;
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis;
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout;
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider;
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent;
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders;
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer;
import kotlinx.coroutines.Dispatchers;
import org.tensorflow.lite.Interpreter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import androidx.compose.ui.graphics.Shape;
import com.jpbaiense.driverhealthapp.R;
import com.jpbaiense.driverhealthapp.presentation.permissions.PermissionUtils;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0007\u001aE\u0010\u0005\u001a\u00020\u00012\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010\u001a@\u0010\u0011\u001a\u00020\u00012\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u00132\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00132\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00040\u0013H\u0007\u001a\b\u0010\u0017\u001a\u00020\u0001H\u0007\u001aM\u0010\u0018\u001a\u00020\u00012\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001c0\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0007\u00a2\u0006\u0002\u0010!\u001a(\u0010\"\u001a\u00020\u00012\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00010$2\u0006\u0010\u001f\u001a\u00020 2\b\b\u0002\u0010%\u001a\u00020&H\u0007\u001a/\u0010\'\u001a\u00020\u00012\u0012\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u001c0\u001c2\u000e\u0010(\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010*0)\u00a2\u0006\u0002\u0010+\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b\u0019\u00a8\u0006,"}, d2 = {"BatteryLevel", "", "soc", "", "", "CustomLinearProgressIndicator", "modifier", "Landroidx/compose/ui/Modifier;", "progress", "", "progressColor", "Landroidx/compose/ui/graphics/Color;", "backgroundColor", "clipShape", "Landroidx/compose/ui/graphics/Shape;", "CustomLinearProgressIndicator-OoHUuok", "(Landroidx/compose/ui/Modifier;FJJLandroidx/compose/ui/graphics/Shape;)V", "DiagnosticsSection", "ppgValueList", "Landroidx/compose/runtime/snapshots/SnapshotStateList;", "xAxisValueList", "yAxisValueList", "zAxisValueList", "GreetingSection", "HealthDataBatterySection", "temperature", "pressure", "modelInputData", "", "isDataReadyToModel", "", "context", "Landroid/content/Context;", "(Ljava/util/List;Ljava/util/List;[[Ljava/lang/Float;ZLandroid/content/Context;)V", "HomeScreen", "onBluetoothStateChanged", "Lkotlin/Function0;", "viewModel", "Lcom/jpbaiense/driverhealthapp/presentation/DriveHealthViewModel;", "ModelEstimate", "outputText", "Landroidx/compose/runtime/MutableState;", "", "([[Ljava/lang/Float;Landroidx/compose/runtime/MutableState;)V", "app_debug"})
public final class HomeScreenKt {
    
    @androidx.compose.runtime.Composable
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onBluetoothStateChanged, @org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.presentation.DriveHealthViewModel viewModel) {
    }
    
    public static final void ModelEstimate(@org.jetbrains.annotations.NotNull
    java.lang.Float[][] modelInputData, @org.jetbrains.annotations.NotNull
    androidx.compose.runtime.MutableState<java.lang.String> outputText) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void GreetingSection() {
    }
    
    @androidx.compose.runtime.Composable
    public static final void DiagnosticsSection(@org.jetbrains.annotations.NotNull
    androidx.compose.runtime.snapshots.SnapshotStateList<java.lang.Integer> ppgValueList, @org.jetbrains.annotations.NotNull
    androidx.compose.runtime.snapshots.SnapshotStateList<java.lang.Integer> xAxisValueList, @org.jetbrains.annotations.NotNull
    androidx.compose.runtime.snapshots.SnapshotStateList<java.lang.Integer> yAxisValueList, @org.jetbrains.annotations.NotNull
    androidx.compose.runtime.snapshots.SnapshotStateList<java.lang.Integer> zAxisValueList) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void HealthDataBatterySection(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> temperature, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> pressure, @org.jetbrains.annotations.NotNull
    java.lang.Float[][] modelInputData, boolean isDataReadyToModel, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
    }
    
    @androidx.compose.runtime.Composable
    public static final void BatteryLevel(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> soc) {
    }
}