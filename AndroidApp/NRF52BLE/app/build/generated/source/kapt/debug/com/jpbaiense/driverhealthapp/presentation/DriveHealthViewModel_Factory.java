package com.jpbaiense.driverhealthapp.presentation;

import com.jpbaiense.driverhealthapp.data.DriveHealthReceiveManager;
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
public final class DriveHealthViewModel_Factory implements Factory<DriveHealthViewModel> {
  private final Provider<DriveHealthReceiveManager> driveHealthReceiveManagerProvider;

  public DriveHealthViewModel_Factory(
      Provider<DriveHealthReceiveManager> driveHealthReceiveManagerProvider) {
    this.driveHealthReceiveManagerProvider = driveHealthReceiveManagerProvider;
  }

  @Override
  public DriveHealthViewModel get() {
    return newInstance(driveHealthReceiveManagerProvider.get());
  }

  public static DriveHealthViewModel_Factory create(
      Provider<DriveHealthReceiveManager> driveHealthReceiveManagerProvider) {
    return new DriveHealthViewModel_Factory(driveHealthReceiveManagerProvider);
  }

  public static DriveHealthViewModel newInstance(
      DriveHealthReceiveManager driveHealthReceiveManager) {
    return new DriveHealthViewModel(driveHealthReceiveManager);
  }
}
