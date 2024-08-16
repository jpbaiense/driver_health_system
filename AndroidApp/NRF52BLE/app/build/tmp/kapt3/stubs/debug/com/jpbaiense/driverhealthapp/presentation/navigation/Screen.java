package com.jpbaiense.driverhealthapp.presentation.navigation;

import android.content.Context;
import androidx.compose.runtime.Composable;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0002\u0007\bB\u000f\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u0082\u0001\u0002\t\n\u00a8\u0006\u000b"}, d2 = {"Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen;", "", "route", "", "(Ljava/lang/String;)V", "getRoute", "()Ljava/lang/String;", "HomeScreen", "StartScreen", "Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen$HomeScreen;", "Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen$StartScreen;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String route = null;
    
    private Screen(java.lang.String route) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getRoute() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen$StartScreen;", "Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen;", "()V", "app_debug"})
    public static final class StartScreen extends com.jpbaiense.driverhealthapp.presentation.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.presentation.navigation.Screen.StartScreen INSTANCE = null;
        
        private StartScreen() {
            super(null);
        }
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen$HomeScreen;", "Lcom/jpbaiense/driverhealthapp/presentation/navigation/Screen;", "()V", "app_debug"})
    public static final class HomeScreen extends com.jpbaiense.driverhealthapp.presentation.navigation.Screen {
        @org.jetbrains.annotations.NotNull
        public static final com.jpbaiense.driverhealthapp.presentation.navigation.Screen.HomeScreen INSTANCE = null;
        
        private HomeScreen() {
            super(null);
        }
    }
}