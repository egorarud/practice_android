package com.example.practice.di;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "javax.inject.Named",
    "dagger.hilt.android.qualifiers.ApplicationContext"
})
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ProfileProvideModule_ProvideProfileDataStoreFactory implements Factory<DataStore<Preferences>> {
  private final Provider<Context> contextProvider;

  public ProfileProvideModule_ProvideProfileDataStoreFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DataStore<Preferences> get() {
    return provideProfileDataStore(contextProvider.get());
  }

  public static ProfileProvideModule_ProvideProfileDataStoreFactory create(
      Provider<Context> contextProvider) {
    return new ProfileProvideModule_ProvideProfileDataStoreFactory(contextProvider);
  }

  public static DataStore<Preferences> provideProfileDataStore(Context context) {
    return Preconditions.checkNotNullFromProvides(ProfileProvideModule.INSTANCE.provideProfileDataStore(context));
  }
}
