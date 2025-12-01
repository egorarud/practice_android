package com.example.practice.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class ProfileRepositoryImpl_Factory implements Factory<ProfileRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public ProfileRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public ProfileRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static ProfileRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new ProfileRepositoryImpl_Factory(dataStoreProvider);
  }

  public static ProfileRepositoryImpl newInstance(DataStore<Preferences> dataStore) {
    return new ProfileRepositoryImpl(dataStore);
  }
}
