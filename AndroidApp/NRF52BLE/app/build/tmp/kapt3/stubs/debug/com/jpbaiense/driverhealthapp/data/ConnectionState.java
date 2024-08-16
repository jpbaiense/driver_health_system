package com.jpbaiense.driverhealthapp.data;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u00002\u00020\u0001:\u0004\u0002\u0003\u0004\u0005\u0082\u0001\u0004\u0006\u0007\b\t\u00a8\u0006\n"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "", "Connected", "CurrentlyInitializing", "Disconnected", "Uninitialized", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Connected;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState$CurrentlyInitializing;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Disconnected;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Uninitialized;", "app_debug"})
public abstract interface ConnectionState {
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Connected;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "()V", "app_debug"})
    public static final class Connected implements com.jpbaiense.driverhealthapp.data.ConnectionState {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.data.ConnectionState.Connected INSTANCE = null;
        
        private Connected() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Disconnected;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "()V", "app_debug"})
    public static final class Disconnected implements com.jpbaiense.driverhealthapp.data.ConnectionState {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.data.ConnectionState.Disconnected INSTANCE = null;
        
        private Disconnected() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ConnectionState$Uninitialized;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "()V", "app_debug"})
    public static final class Uninitialized implements com.jpbaiense.driverhealthapp.data.ConnectionState {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.data.ConnectionState.Uninitialized INSTANCE = null;
        
        private Uninitialized() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/data/ConnectionState$CurrentlyInitializing;", "Lcom/jpbaiense/driverhealthapp/data/ConnectionState;", "()V", "app_debug"})
    public static final class CurrentlyInitializing implements com.jpbaiense.driverhealthapp.data.ConnectionState {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.data.ConnectionState.CurrentlyInitializing INSTANCE = null;
        
        private CurrentlyInitializing() {
            super();
        }
    }
}