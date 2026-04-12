package com.locapin.mobile.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.locapin.mobile.data.local.SanJuanSeedDataSource;
import com.locapin.mobile.data.remote.LocaPinApi;
import com.locapin.mobile.feature.admin.AdminAttractionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SegmentedMapRepositoryImpl_Factory implements Factory<SegmentedMapRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<LocaPinApi> apiProvider;

  private final Provider<SanJuanSeedDataSource> seedDataSourceProvider;

  private final Provider<AdminAttractionRepository> adminAttractionRepositoryProvider;

  private SegmentedMapRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<LocaPinApi> apiProvider, Provider<SanJuanSeedDataSource> seedDataSourceProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider) {
    this.firestoreProvider = firestoreProvider;
    this.apiProvider = apiProvider;
    this.seedDataSourceProvider = seedDataSourceProvider;
    this.adminAttractionRepositoryProvider = adminAttractionRepositoryProvider;
  }

  @Override
  public SegmentedMapRepositoryImpl get() {
    return newInstance(firestoreProvider.get(), apiProvider.get(), seedDataSourceProvider.get(), adminAttractionRepositoryProvider.get());
  }

  public static SegmentedMapRepositoryImpl_Factory create(
      Provider<FirebaseFirestore> firestoreProvider, Provider<LocaPinApi> apiProvider,
      Provider<SanJuanSeedDataSource> seedDataSourceProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider) {
    return new SegmentedMapRepositoryImpl_Factory(firestoreProvider, apiProvider, seedDataSourceProvider, adminAttractionRepositoryProvider);
  }

  public static SegmentedMapRepositoryImpl newInstance(FirebaseFirestore firestore, LocaPinApi api,
      SanJuanSeedDataSource seedDataSource, AdminAttractionRepository adminAttractionRepository) {
    return new SegmentedMapRepositoryImpl(firestore, api, seedDataSource, adminAttractionRepository);
  }
}
