package com.jpbaiense.driverhealthapp.data;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u000bH\u00c6\u0003JY\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010#\u001a\u00020\u0003H\u00d6\u0001J\t\u0010$\u001a\u00020%H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010\u00a8\u0006&"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/DriveHealthResult;", "", "ppgValueON", "", "xAxisData", "yAxisData", "zAxisData", "temperature", "pressure", "soc", "connectionState", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "(IIIIIIILcom/jpbaiense/driverhealthapp/data/ConnectionState;)V", "getConnectionState", "()Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "getPpgValueON", "()I", "getPressure", "getSoc", "getTemperature", "getXAxisData", "getYAxisData", "getZAxisData", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class DriveHealthResult {
    private final int ppgValueON = 0;
    private final int xAxisData = 0;
    private final int yAxisData = 0;
    private final int zAxisData = 0;
    private final int temperature = 0;
    private final int pressure = 0;
    private final int soc = 0;
    @org.jetbrains.annotations.NotNull
    private final com.jpbaiense.driverhealthapp.data.ConnectionState connectionState = null;
    
    @org.jetbrains.annotations.NotNull
    public final com.jpbaiense.driverhealthapp.data.DriveHealthResult copy(int ppgValueON, int xAxisData, int yAxisData, int zAxisData, int temperature, int pressure, int soc, @org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.data.ConnectionState connectionState) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public DriveHealthResult(int ppgValueON, int xAxisData, int yAxisData, int zAxisData, int temperature, int pressure, int soc, @org.jetbrains.annotations.NotNull
    com.jpbaiense.driverhealthapp.data.ConnectionState connectionState) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int getPpgValueON() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int getXAxisData() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int getYAxisData() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int getZAxisData() {
        return 0;
    }
    
    public final int component5() {
        return 0;
    }
    
    public final int getTemperature() {
        return 0;
    }
    
    public final int component6() {
        return 0;
    }
    
    public final int getPressure() {
        return 0;
    }
    
    public final int component7() {
        return 0;
    }
    
    public final int getSoc() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.jpbaiense.driverhealthapp.data.ConnectionState component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.jpbaiense.driverhealthapp.data.ConnectionState getConnectionState() {
        return null;
    }
}