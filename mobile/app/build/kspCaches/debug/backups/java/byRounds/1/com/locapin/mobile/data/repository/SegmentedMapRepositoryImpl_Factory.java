package com.locapin.mobile.data.repository;

import com.locapin.mobile.data.local.SanJuanSeedDataSource;
import com.locapin.mobile.data.remote.LocaPinApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SegmentedMapRepositoryImpl_Factory implements Factory<SegmentedMapRepositoryImpl> {
  private final Provider<LocaPinApi> apiProvider;

  private final Provider<SanJuanSeedDataSource> seedDataSourceProvider;

  public SegmentedMapRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider,
      Provider<SanJuanSeedDataSource> seedDataSourceProvider) {
    this.apiProvider = apiProvider;
    this.seedDataSourceProvider = seedDataSourceProvider;
  }

  @Override
  public SegmentedMapRepositoryImpl get() {
    return newInstance(apiProvider.get(), seedDataSourceProvider.get());
  }

  public static SegmentedMapRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider,
      Provider<SanJuanSeedDataSource> seedDataSourceProvider) {
    return new SegmentedMapRepositoryImpl_Factory(apiProvider, seedDataSourceProvider);
  }

  public static SegmentedMapRepositoryImpl newInstance(LocaPinApi api,
      SanJuanSeedDataSource seedDataSource) {
    return new SegmentedMapRepositoryImpl(api, seedDataSource);
  }
}
