package com.locapin.mobile.ui;

import com.locapin.mobile.core.datastore.UserPreferencesDataStore;
import com.locapin.mobile.core.network.ConnectivityStatusHelper;
import com.locapin.mobile.domain.repository.AuthRepository;
import com.locapin.mobile.domain.repository.DestinationRepository;
import com.locapin.mobile.domain.repository.ProfileRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<DestinationRepository> destinationRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<TouristFavoritesRepository> favoritesRepositoryProvider;

  private final Provider<ConnectivityStatusHelper> connectivityStatusHelperProvider;

  private MainViewModel_Factory(Provider<UserPreferencesDataStore> prefsProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider,
      Provider<ConnectivityStatusHelper> connectivityStatusHelperProvider) {
    this.prefsProvider = prefsProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.destinationRepositoryProvider = destinationRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.favoritesRepositoryProvider = favoritesRepositoryProvider;
    this.connectivityStatusHelperProvider = connectivityStatusHelperProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(prefsProvider.get(), authRepositoryProvider.get(), destinationRepositoryProvider.get(), profileRepositoryProvider.get(), favoritesRepositoryProvider.get(), connectivityStatusHelperProvider.get());
  }

  public static MainViewModel_Factory create(Provider<UserPreferencesDataStore> prefsProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<TouristFavoritesRepository> favoritesRepositoryProvider,
      Provider<ConnectivityStatusHelper> connectivityStatusHelperProvider) {
    return new MainViewModel_Factory(prefsProvider, authRepositoryProvider, destinationRepositoryProvider, profileRepositoryProvider, favoritesRepositoryProvider, connectivityStatusHelperProvider);
  }

  public static MainViewModel newInstance(UserPreferencesDataStore prefs,
      AuthRepository authRepository, DestinationRepository destinationRepository,
      ProfileRepository profileRepository, TouristFavoritesRepository favoritesRepository,
      ConnectivityStatusHelper connectivityStatusHelper) {
    return new MainViewModel(prefs, authRepository, destinationRepository, profileRepository, favoritesRepository, connectivityStatusHelper);
  }
}
