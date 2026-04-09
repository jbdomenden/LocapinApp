package com.locapin.mobile.feature.map;

import com.locapin.mobile.core.location.LocationProvider;
import com.locapin.mobile.domain.repository.DestinationRepository;
import com.locapin.mobile.domain.repository.HistoryRepository;
import com.locapin.mobile.domain.repository.SegmentedMapRepository;
import com.locapin.mobile.domain.repository.TouristFavoritesRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SegmentedMapViewModel_Factory implements Factory<SegmentedMapViewModel> {
  private final Provider<SegmentedMapRepository> repositoryProvider;

  private final Provider<LocationProvider> locationProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  private final Provider<DestinationRepository> destinationRepositoryProvider;

  private final Provider<TouristFavoritesRepository> favoritesRepositoryProvider;

  private SegmentedMapViewModel_Factory(Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.locationProvider = locationProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
    this.destinationRepositoryProvider = destinationRepositoryProvider;
    this.favoritesRepositoryProvider = favoritesRepositoryProvider;
  }

  @Override
  public SegmentedMapViewModel get() {
    return newInstance(repositoryProvider.get(), locationProvider.get(), historyRepositoryProvider.get(), destinationRepositoryProvider.get(), favoritesRepositoryProvider.get());
  }

  public static SegmentedMapViewModel_Factory create(
      Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<HistoryRepository> historyRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider) {
    return new SegmentedMapViewModel_Factory(repositoryProvider, locationProvider, historyRepositoryProvider, destinationRepositoryProvider, favoritesRepositoryProvider);
  }

  public static SegmentedMapViewModel newInstance(SegmentedMapRepository repository,
      LocationProvider locationProvider, HistoryRepository historyRepository,
      DestinationRepository destinationRepository, TouristFavoritesRepository favoritesRepository) {
    return new SegmentedMapViewModel(repository, locationProvider, historyRepository, destinationRepository, favoritesRepository);
  }
}
