package com.locapin.mobile.core.network;

import com.locapin.mobile.core.datastore.UserPreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpFactory implements Factory<OkHttpClient> {
  private final Provider<UserPreferencesDataStore> prefsProvider;

  public NetworkModule_ProvideOkHttpFactory(Provider<UserPreferencesDataStore> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttp(prefsProvider.get());
  }

  public static NetworkModule_ProvideOkHttpFactory create(
      Provider<UserPreferencesDataStore> prefsProvider) {
    return new NetworkModule_ProvideOkHttpFactory(prefsProvider);
  }

  public static OkHttpClient provideOkHttp(UserPreferencesDataStore prefs) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttp(prefs));
  }
}
