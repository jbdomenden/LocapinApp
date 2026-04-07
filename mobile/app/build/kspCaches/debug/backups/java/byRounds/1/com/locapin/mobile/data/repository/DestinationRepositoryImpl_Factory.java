package com.locapin.mobile.data.repository;

import com.locapin.mobile.data.remote.LocaPinApi;
import com.locapin.mobile.domain.repository.TouristFavoritesRepository;
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
public final class DestinationRepositoryImpl_Factory implements Factory<DestinationRepositoryImpl> {
  private final Provider<LocaPinApi> apiProvider;

  private final Provider<AdminAttractionRepository> adminAttractionRepositoryProvider;

  private final Provider<TouristFavoritesRepository> favoritesRepositoryProvider;

  public DestinationRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider) {
    this.apiProvider = apiProvider;
    this.adminAttractionRepositoryProvider = adminAttractionRepositoryProvider;
    this.favoritesRepositoryProvider = favoritesRepositoryProvider;
  }

  @Override
  public DestinationRepositoryImpl get() {
    return newInstance(apiProvider.get(), adminAttractionRepositoryProvider.get(), favoritesRepositoryProvider.get());
  }

  public static DestinationRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider,
      Provider<AdminAttractionRepository> adminAttractionRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider) {
    return new DestinationRepositoryImpl_Factory(apiProvider, adminAttractionRepositoryProvider, favoritesRepositoryProvider);
  }

  public static DestinationRepositoryImpl newInstance(LocaPinApi api,
      AdminAttractionRepository adminAttractionRepository,
      TouristFavoritesRepository favoritesRepository) {
    return new DestinationRepositoryImpl(api, adminAttractionRepository, favoritesRepository);
  }
}
