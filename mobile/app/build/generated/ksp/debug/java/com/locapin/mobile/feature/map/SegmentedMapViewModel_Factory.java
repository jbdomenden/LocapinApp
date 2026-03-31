package com.locapin.mobile.feature.map;

import com.locapin.mobile.core.location.LocationProvider;
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

  public SegmentedMapViewModel_Factory(Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider) {
    this.repositoryProvider = repositoryProvider;
    this.locationProvider = locationProvider;
  }

  @Override
  public SegmentedMapViewModel get() {
    return newInstance(repositoryProvider.get(), locationProvider.get());
  }

  public static SegmentedMapViewModel_Factory create(
      Provider<SegmentedMapRepository> repositoryProvider,
      Provider<LocationProvider> locationProvider) {
    return new SegmentedMapViewModel_Factory(repositoryProvider, locationProvider);
  }

  public static SegmentedMapViewModel newInstance(SegmentedMapRepository repository,
      LocationProvider locationProvider) {
    return new SegmentedMapViewModel(repository, locationProvider);
  }
}
