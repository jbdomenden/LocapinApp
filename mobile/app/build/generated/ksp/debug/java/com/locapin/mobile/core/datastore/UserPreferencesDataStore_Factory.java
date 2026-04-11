package com.locapin.mobile.core.datastore;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UserPreferencesDataStore_Factory implements Factory<UserPreferencesDataStore> {
  private final Provider<Context> contextProvider;

  private UserPreferencesDataStore_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserPreferencesDataStore get() {
    return newInstance(contextProvider.get());
  }

  public static UserPreferencesDataStore_Factory create(Provider<Context> contextProvider) {
    return new UserPreferencesDataStore_Factory(contextProvider);
  }

  public static UserPreferencesDataStore newInstance(Context context) {
    return new UserPreferencesDataStore(context);
  }
}
