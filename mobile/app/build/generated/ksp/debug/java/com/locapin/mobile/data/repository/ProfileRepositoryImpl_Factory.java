package com.locapin.mobile.data.repository;

import com.locapin.mobile.data.remote.LocaPinApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ProfileRepositoryImpl_Factory implements Factory<ProfileRepositoryImpl> {
  private final Provider<LocaPinApi> apiProvider;

  private ProfileRepositoryImpl_Factory(Provider<LocaPinApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ProfileRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static ProfileRepositoryImpl_Factory create(Provider<LocaPinApi> apiProvider) {
    return new ProfileRepositoryImpl_Factory(apiProvider);
  }

  public static ProfileRepositoryImpl newInstance(LocaPinApi api) {
    return new ProfileRepositoryImpl(api);
  }
}
