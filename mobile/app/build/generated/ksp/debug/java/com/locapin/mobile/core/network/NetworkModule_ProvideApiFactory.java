package com.locapin.mobile.core.network;

import com.locapin.mobile.data.remote.LocaPinApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
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
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NetworkModule_ProvideApiFactory implements Factory<LocaPinApi> {
  private final Provider<OkHttpClient> clientProvider;

  private final Provider<Json> jsonProvider;

  private NetworkModule_ProvideApiFactory(Provider<OkHttpClient> clientProvider,
      Provider<Json> jsonProvider) {
    this.clientProvider = clientProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public LocaPinApi get() {
    return provideApi(clientProvider.get(), jsonProvider.get());
  }

  public static NetworkModule_ProvideApiFactory create(Provider<OkHttpClient> clientProvider,
      Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideApiFactory(clientProvider, jsonProvider);
  }

  public static LocaPinApi provideApi(OkHttpClient client, Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideApi(client, json));
  }
}
