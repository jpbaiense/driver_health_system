package com.jpbaiense.driverhealthapp.presentation;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.util.Log;
import androidx.compose.foundation.layout.*;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.style.TextAlign;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import com.jpbaiense.driverhealthapp.data.ConnectionState;
import com.google.accompanist.permissions.ExperimentalPermissionsApi;
import com.jpbaiense.driverhealthapp.presentation.permissions.PermissionUtils;
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis;
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout;
import com.patrykandpatrick.vico.core.chart.values.AxisValueOverrider;
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent;
import com.patrykandpatrick.vico.core.component.shape.Shapes;
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders;
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u0006\u0006"}, d2 = {"TemperatureHumidityScreen", "", "onBluetoothStateChanged", "Lkotlin/Function0;", "viewModel", "Lcom/jpbaiense/driverhealthapp/presentation/DriveHealthViewModel;", "app_debug"})
public final class DriveHealthScreenKt {
    
    @androidx.compose.runtime.Composable
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    public static final void TemperatureHumidityScreen(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onBluetoothStateChanged, @org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.presentation.DriveHealthViewModel viewModel) {
    }
}