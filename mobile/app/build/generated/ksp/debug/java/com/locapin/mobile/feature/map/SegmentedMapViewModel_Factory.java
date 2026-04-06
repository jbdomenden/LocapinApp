package com.locapin.mobile.feature.map;

import com.locapin.mobile.core.location.LocationProvider;
import com.locapin.mobile.domain.repository.HistoryRepository;
import com.locapin.mobile.domain.repository.SegmentedMapRepository;
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
    "cast",
    "deprecation"
})
public final class SegmentedMapViewModel_Factory implements Factory<SegmentedMapViewModel> {
  private final Provider<SegmentedMapRepository> repositoryProvider;

  private final Provider<LocationProvider> locationProvider;

  private final Provider<HistoryRepository> historyRepositoryProvider;

  public SegmentedMapViewModel_Factory(Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.locationProvider = locationProvider;
    this.historyRepositoryProvider = historyRepositoryProvider;
  }

  @Override
  public SegmentedMapViewModel get() {
    return newInstance(repositoryProvider.get(), locationProvider.get(), historyRepositoryProvider.get());
  }

  public static SegmentedMapViewModel_Factory create(
      Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider,
      Provider<HistoryRepository> historyRepositoryProvider) {
    return new SegmentedMapViewModel_Factory(repositoryProvider, locationProvider, historyRepositoryProvider);
  }

  public static SegmentedMapViewModel newInstance(SegmentedMapRepository repository,
      LocationProvider locationProvider, HistoryRepository historyRepository) {
    return new SegmentedMapViewModel(repository, locationProvider, historyRepository);
  }
}
