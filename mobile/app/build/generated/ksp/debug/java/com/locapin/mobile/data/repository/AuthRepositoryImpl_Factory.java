package com.locapin.mobile.data.repository;

import com.locapin.mobile.core.datastore.UserPreferencesDataStore;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<LocaPinApi> apiProvider;

  private final Provider<UserPreferencesDataStore> prefsProvider;

  public AuthRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider,
      Provider<UserPreferencesDataStore> prefsProvider) {
    this.apiProvider = apiProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), prefsProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider,
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, prefsProvider);
  }

  public static AuthRepositoryImpl newInstance(LocaPinApi api, UserPreferencesDataStore prefs) {
    return new AuthRepositoryImpl(api, prefs);
  }
}
