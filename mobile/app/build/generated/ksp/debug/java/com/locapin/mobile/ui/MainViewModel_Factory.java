package com.locapin.mobile.ui;

import com.locapin.mobile.core.datastore.UserPreferencesDataStore;
import com.locapin.mobile.domain.repository.AuthRepository;
import com.locapin.mobile.domain.repository.DestinationRepository;
import com.locapin.mobile.domain.repository.ProfileRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<DestinationRepository> destinationRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  public MainViewModel_Factory(Provider<UserPreferencesDataStore> prefsProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    this.prefsProvider = prefsProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.destinationRepositoryProvider = destinationRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(prefsProvider.get(), authRepositoryProvider.get(), destinationRepositoryProvider.get(), profileRepositoryProvider.get());
  }

  public static MainViewModel_Factory create(Provider<UserPreferencesDataStore> prefsProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DestinationRepository> destinationRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new MainViewModel_Factory(prefsProvider, authRepositoryProvider, destinationRepositoryProvider, profileRepositoryProvider);
  }

  public static MainViewModel newInstance(UserPreferencesDataStore prefs,
      AuthRepository authRepository, DestinationRepository destinationRepository,
      ProfileRepository profileRepository) {
    return new MainViewModel(prefs, authRepository, destinationRepository, profileRepository);
  }
}
