package com.locapin.mobile.domain.usecase;

import com.locapin.mobile.domain.repository.AuthRepository;
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
public final class RegisterUseCase_Factory implements Factory<RegisterUseCase> {
  private final Provider<AuthRepository> repoProvider;

  public RegisterUseCase_Factory(Provider<AuthRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public RegisterUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static RegisterUseCase_Factory create(Provider<AuthRepository> repoProvider) {
    return new RegisterUseCase_Factory(repoProvider);
  }

  public static RegisterUseCase newInstance(AuthRepository repo) {
    return new RegisterUseCase(repo);
  }
}
