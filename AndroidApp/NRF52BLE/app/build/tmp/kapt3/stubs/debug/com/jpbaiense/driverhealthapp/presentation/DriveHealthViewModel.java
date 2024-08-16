package com.jpbaiense.driverhealthapp.presentation;

import androidx.lifecycle.ViewModel;
import com.jpbaiense.driverhealthapp.data.ConnectionState;
import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager;
import com.jpbaiense.driverhealthapp.util.Resource;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@dagger.hilt.android.lifecycle.HiltViewModel
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u001f\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u00108\u001a\u000209J\u0006\u0010:\u001a\u000209J\b\u0010;\u001a\u000209H\u0014J\u0006\u0010<\u001a\u000209J\u0006\u0010=\u001a\u000209J\b\u0010>\u001a\u000209H\u0002R+\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u00068F@FX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\f\u0010\r\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R/\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0005\u001a\u0004\u0018\u00010\u000e8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0014\u0010\r\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R/\u0010\u0015\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0005\u001a\u0004\u0018\u00010\u000e8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0018\u0010\r\u001a\u0004\b\u0016\u0010\u0011\"\u0004\b\u0017\u0010\u0013R+\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u001f\u0010\r\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR+\u0010 \u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b#\u0010\r\u001a\u0004\b!\u0010\u001c\"\u0004\b\"\u0010\u001eR+\u0010$\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\'\u0010\r\u001a\u0004\b%\u0010\u001c\"\u0004\b&\u0010\u001eR+\u0010(\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b+\u0010\r\u001a\u0004\b)\u0010\u001c\"\u0004\b*\u0010\u001eR+\u0010,\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b/\u0010\r\u001a\u0004\b-\u0010\u001c\"\u0004\b.\u0010\u001eR+\u00100\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b3\u0010\r\u001a\u0004\b1\u0010\u001c\"\u0004\b2\u0010\u001eR+\u00104\u001a\u00020\u00192\u0006\u0010\u0005\u001a\u00020\u00198F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b7\u0010\r\u001a\u0004\b5\u0010\u001c\"\u0004\b6\u0010\u001e\u00a8\u0006?"}, d2 = {"Lcom/jpbaiense/driverhealthapp/presentation/DriveHealthViewModel;", "Landroidx/lifecycle/ViewModel;", "driveHealthReceiveManager", "Lcom/jpbaiense/driverhealthapp/data/DriveHealthReceiveManager;", "(Lcom/jpbaiense/driverhealthapp/data/DriveHealthReceiveManager;)V", "<set-?>", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "connectionState", "getConnectionState", "()Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "setConnectionState", "(Lcom/jpbaiense/driverhealthapp/data/ConnectionState;)V", "connectionState$delegate", "Landroidx/compose/runtime/MutableState;", "", "errorMessage", "getErrorMessage", "()Ljava/lang/String;", "setErrorMessage", "(Ljava/lang/String;)V", "errorMessage$delegate", "initializingMessage", "getInitializingMessage", "setInitializingMessage", "initializingMessage$delegate", "", "ppg", "getPpg", "()I", "setPpg", "(I)V", "ppg$delegate", "pressure", "getPressure", "setPressure", "pressure$delegate", "soc", "getSoc", "setSoc", "soc$delegate", "temperature", "getTemperature", "setTemperature", "temperature$delegate", "xAxis", "getXAxis", "setXAxis", "xAxis$delegate", "yAxis", "getYAxis", "setYAxis", "yAxis$delegate", "zAxis", "getZAxis", "setZAxis", "zAxis$delegate", "disconnect", "", "initializeConnection", "onCleared", "read", "reconnect", "subscribeToChanges", "app_debug"})
public final class DriveHealthViewModel extends androidx.lifecycle.ViewModel {
    private final com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager driveHealthReceiveManager = null;
    @org.jetbrains.annotations.Nullable
    private final androidx.compose.runtime.MutableState initializingMessage$delegate = null;
    @org.jetbrains.annotations.Nullable
    private final androidx.compose.runtime.MutableState errorMessage$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState ppg$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState xAxis$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState yAxis$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState zAxis$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState temperature$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState pressure$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState soc$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.compose.runtime.MutableState connectionState$delegate = null;
    
    @javax.inject.Inject
    public DriveHealthViewModel(@org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager driveHealthReceiveManager) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getInitializingMessage() {
        return null;
    }
    
    private final void setInitializingMessage(java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    private final void setErrorMessage(java.lang.String p0) {
    }
    
    public final int getPpg() {
        return 0;
    }
    
    private final void setPpg(int p0) {
    }
    
    public final int getXAxis() {
        return 0;
    }
    
    private final void setXAxis(int p0) {
    }
    
    public final int getYAxis() {
        return 0;
    }
    
    private final void setYAxis(int p0) {
    }
    
    public final int getZAxis() {
        return 0;
    }
    
    private final void setZAxis(int p0) {
    }
    
    public final int getTemperature() {
        return 0;
    }
    
    private final void setTemperature(int p0) {
    }
    
    public final int getPressure() {
        return 0;
    }
    
    private final void setPressure(int p0) {
    }
    
    public final int getSoc() {
        return 0;
    }
    
    private final void setSoc(int p0) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.jpbaiense.driverhealthapp.data.ConnectionState getConnectionState() {
        return null;
    }
    
    public final void setConnectionState(@org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.data.ConnectionState p0) {
    }
    
    private final void subscribeToChanges() {
    }
    
    public final void disconnect() {
    }
    
    public final void reconnect() {
    }
    
    public final void initializeConnection() {
    }
    
    @java.lang.Override
    protected void onCleared() {
    }
    
    public final void read() {
    }
}