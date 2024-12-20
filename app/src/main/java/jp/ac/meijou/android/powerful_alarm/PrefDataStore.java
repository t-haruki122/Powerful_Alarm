package jp.ac.meijou.android.powerful_alarm;

import android.content.Context;

import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.datastore.preferences.core.Preferences;

import java.util.Optional;

import io.reactivex.rxjava3.core.Single;


public class PrefDataStore {
    // 地域情報を保持するためのデータストア(シングルトン)

    private static PrefDataStore instance;
    private final RxDataStore<Preferences> dataStore;

    private PrefDataStore(RxDataStore<Preferences> dataStore) {
        this.dataStore = dataStore;
    }

    public static PrefDataStore getInstance(Context context) {
        if (instance == null) {
            var dataStore = new RxPreferenceDataStoreBuilder(
                    context.getApplicationContext(), "areaSettings").build();
            instance = new PrefDataStore(dataStore);
        }
        return instance;
    }

    public void setString(String key, String value) {
        dataStore.updateDataAsync(prefsIn -> {
            var mutablePreferences = prefsIn.toMutablePreferences();
            var prefKey = PreferencesKeys.stringKey(key);

            mutablePreferences.set(prefKey, value);
            return Single.just(mutablePreferences);
        }).subscribe();
    }

    public Optional<String> getString(String key) {
        return dataStore.data()
                .map(prefs -> {
                    var prefKey = PreferencesKeys.stringKey(key);
                    return Optional.ofNullable(prefs.get(prefKey));
                })
                .blockingFirst();
    }
}
