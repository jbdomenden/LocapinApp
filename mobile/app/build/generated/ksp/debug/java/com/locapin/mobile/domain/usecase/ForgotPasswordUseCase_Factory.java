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
public final class ForgotPasswordUseCase_Factory implements Factory<ForgotPasswordUseCase> {
  private final Provider<AuthRepository> repoProvider;

  public ForgotPasswordUseCase_Factory(Provider<AuthRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ForgotPasswordUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static ForgotPasswordUseCase_Factory create(Provider<AuthRepository> repoProvider) {
    return new ForgotPasswordUseCase_Factory(repoProvider);
  }

  public static ForgotPasswordUseCase newInstance(AuthRepository repo) {
    return new ForgotPasswordUseCase(repo);
  }
}
