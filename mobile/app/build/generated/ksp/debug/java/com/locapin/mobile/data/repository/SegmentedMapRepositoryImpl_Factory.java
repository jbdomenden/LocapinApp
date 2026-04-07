package com.locapin.mobile.data.repository;

import com.locapin.mobile.data.local.SanJuanSeedDataSource;
import com.locapin.mobile.data.remote.LocaPinApi;
import com.locapin.mobile.feature.admin.AdminAttractionRepository;
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

  private final Provider<AdminAttractionRepository> adminAttractionRepositoryProvider;

  public SegmentedMapRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider,
      Provider<SanJuanSeedDataSource> seedDataSourceProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider) {
    this.apiProvider = apiProvider;
    this.seedDataSourceProvider = seedDataSourceProvider;
    this.adminAttractionRepositoryProvider = adminAttractionRepositoryProvider;
  }

  @Override
  public SegmentedMapRepositoryImpl get() {
    return newInstance(apiProvider.get(), seedDataSourceProvider.get(), adminAttractionRepositoryProvider.get());
  }

  public static SegmentedMapRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider,
      Provider<SanJuanSeedDataSource> seedDataSourceProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider) {
    return new SegmentedMapRepositoryImpl_Factory(apiProvider, seedDataSourceProvider, adminAttractionRepositoryProvider);
  }

  public static SegmentedMapRepositoryImpl newInstance(LocaPinApi api,
      SanJuanSeedDataSource seedDataSource, AdminAttractionRepository adminAttractionRepository) {
    return new SegmentedMapRepositoryImpl(api, seedDataSource, adminAttractionRepository);
  }
}
