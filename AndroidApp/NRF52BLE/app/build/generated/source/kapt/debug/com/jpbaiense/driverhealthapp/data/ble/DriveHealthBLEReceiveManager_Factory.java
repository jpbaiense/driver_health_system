package com.jpbaiense.driverhealthapp.data.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DriveHealthBLEReceiveManager_Factory implements Factory<DriveHealthBLEReceiveManager> {
  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  private final Provider<Context> contextProvider;

  public DriveHealthBLEReceiveManager_Factory(Provider<BluetoothAdapter> bluetoothAdapterProvider,
      Provider<Context> contextProvider) {
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public DriveHealthBLEReceiveManager get() {
    return newInstance(bluetoothAdapterProvider.get(), contextProvider.get());
  }

  public static DriveHealthBLEReceiveManager_Factory create(
      Provider<BluetoothAdapter> bluetoothAdapterProvider, Provider<Context> contextProvider) {
    return new DriveHealthBLEReceiveManager_Factory(bluetoothAdapterProvider, contextProvider);
  }

  public static DriveHealthBLEReceiveManager newInstance(BluetoothAdapter bluetoothAdapter,
      Context context) {
    return new DriveHealthBLEReceiveManager(bluetoothAdapter, context);
  }
}
