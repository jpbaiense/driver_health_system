package com.jpbaiense.driverhealthapp;

import android.bluetooth.BluetoothAdapter;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  public MainActivity_MembersInjector(Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    return new MainActivity_MembersInjector(bluetoothAdapterProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectBluetoothAdapter(instance, bluetoothAdapterProvider.get());
  }

  @InjectedFieldSignature("com.jpbaiense.driverhealthapp.MainActivity.bluetoothAdapter")
  public static void injectBluetoothAdapter(MainActivity instance,
      BluetoothAdapter bluetoothAdapter) {
    instance.bluetoothAdapter = bluetoothAdapter;
  }
}
