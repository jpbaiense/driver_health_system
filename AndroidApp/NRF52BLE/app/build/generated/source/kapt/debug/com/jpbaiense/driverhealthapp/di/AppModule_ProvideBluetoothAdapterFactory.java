package com.jpbaiense.driverhealthapp.di;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
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
public final class AppModule_ProvideBluetoothAdapterFactory implements Factory<BluetoothAdapter> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideBluetoothAdapterFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BluetoothAdapter get() {
    return provideBluetoothAdapter(contextProvider.get());
  }

  public static AppModule_ProvideBluetoothAdapterFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideBluetoothAdapterFactory(contextProvider);
  }

  public static BluetoothAdapter provideBluetoothAdapter(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBluetoothAdapter(context));
  }
}
