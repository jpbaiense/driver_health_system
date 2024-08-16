package com.jpbaiense.driverhealthapp.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jpbaiense.driverhealthapp.presentation.HomeScreen
import com.jpbaiense.driverhealthapp.presentation.StartScreen
import com.jpbaiense.driverhealthapp.presentation.TemperatureHumidityScreen

@Composable
fun Navigation(
    onBluetoothStateChanged:()->Unit,
    context: Context
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.StartScreen.route){
        composable(Screen.StartScreen.route){
            StartScreen(navController = navController)
        }

//        composable(Screen.TemperatureHumidityScreen.route){
//            TemperatureHumidityScreen(
//                onBluetoothStateChanged
//            )
//        }

        composable(Screen.HomeScreen.route){
            HomeScreen(
                onBluetoothStateChanged,
                context
            )
        }
    }
}

sealed class Screen(val route:String){
    object StartScreen: Screen("start_screen")
//    object TemperatureHumidityScreen: Screen("temp_humid_screen")
    object HomeScreen: Screen("home_screen")
}