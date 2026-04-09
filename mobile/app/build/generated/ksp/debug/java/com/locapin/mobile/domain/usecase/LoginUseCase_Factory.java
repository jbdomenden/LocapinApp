package com.locapin.mobile.domain.usecase;

import com.locapin.mobile.domain.repository.AuthRepository;
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
public final class LoginUseCase_Factory implements Factory<LoginUseCase> {
  private final Provider<AuthRepository> repoProvider;

  private LoginUseCase_Factory(Provider<AuthRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public LoginUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static LoginUseCase_Factory create(Provider<AuthRepository> repoProvider) {
    return new LoginUseCase_Factory(repoProvider);
  }

  public static LoginUseCase newInstance(AuthRepository repo) {
    return new LoginUseCase(repo);
  }
}
