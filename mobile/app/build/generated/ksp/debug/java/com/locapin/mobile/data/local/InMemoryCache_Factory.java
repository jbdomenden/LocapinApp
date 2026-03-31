package com.locapin.mobile.data.local;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
    "deprecation"
})
public final class InMemoryCache_Factory implements Factory<InMemoryCache> {
  @Override
  public InMemoryCache get() {
    return newInstance();
  }

  public static InMemoryCache_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static InMemoryCache newInstance() {
    return new InMemoryCache();
  }

  private static final class InstanceHolder {
    private static final InMemoryCache_Factory INSTANCE = new InMemoryCache_Factory();
  }
}
