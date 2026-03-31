package com.locapin.mobile.data.repository;

import com.locapin.mobile.data.local.InMemoryCache;
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
public final class DestinationRepositoryImpl_Factory implements Factory<DestinationRepositoryImpl> {
  private final Provider<LocaPinApi> apiProvider;

  private final Provider<InMemoryCache> cacheProvider;

  public DestinationRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider,
      Provider<InMemoryCache> cacheProvider) {
    this.apiProvider = apiProvider;
    this.cacheProvider = cacheProvider;
  }

  @Override
  public DestinationRepositoryImpl get() {
    return newInstance(apiProvider.get(), cacheProvider.get());
  }

  public static DestinationRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider,
      Provider<InMemoryCache> cacheProvider) {
    return new DestinationRepositoryImpl_Factory(apiProvider, cacheProvider);
  }

  public static DestinationRepositoryImpl newInstance(LocaPinApi api, InMemoryCache cache) {
    return new DestinationRepositoryImpl(api, cache);
  }
}
