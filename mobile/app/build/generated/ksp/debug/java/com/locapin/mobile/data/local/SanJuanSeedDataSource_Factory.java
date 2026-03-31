package com.locapin.mobile.data.local;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "cast",
    "deprecation"
})
public final class SanJuanSeedDataSource_Factory implements Factory<SanJuanSeedDataSource> {
  @Override
  public SanJuanSeedDataSource get() {
    return newInstance();
  }

  public static SanJuanSeedDataSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SanJuanSeedDataSource newInstance() {
    return new SanJuanSeedDataSource();
  }

  private static final class InstanceHolder {
    private static final SanJuanSeedDataSource_Factory INSTANCE = new SanJuanSeedDataSource_Factory();
  }
}
