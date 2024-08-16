package com.jpbaiense.driverhealthapp.di;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideDriveHealthReceiveManagerFactory implements Factory<DriveHealthReceiveManager> {
  private final Provider<Context> contextProvider;

  private final Provider<BluetoothAdapter> bluetoothAdapterProvider;

  public AppModule_ProvideDriveHealthReceiveManagerFactory(Provider<Context> contextProvider,
      Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    this.contextProvider = contextProvider;
    this.bluetoothAdapterProvider = bluetoothAdapterProvider;
  }

  @Override
  public DriveHealthReceiveManager get() {
    return provideDriveHealthReceiveManager(contextProvider.get(), bluetoothAdapterProvider.get());
  }

  public static AppModule_ProvideDriveHealthReceiveManagerFactory create(
      Provider<Context> contextProvider, Provider<BluetoothAdapter> bluetoothAdapterProvider) {
    return new AppModule_ProvideDriveHealthReceiveManagerFactory(contextProvider, bluetoothAdapterProvider);
  }

  public static DriveHealthReceiveManager provideDriveHealthReceiveManager(Context context,
      BluetoothAdapter bluetoothAdapter) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDriveHealthReceiveManager(context, bluetoothAdapter));
  }
}
