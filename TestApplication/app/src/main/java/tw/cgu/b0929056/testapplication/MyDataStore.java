package tw.cgu.b0929056.testapplication;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;

public class MyDataStore {
    // 定義DataStore的文件名稱
    private static final String FILE_NAME = "MY_APP";
    // 聲明MyDataStore存儲數據的KEY鍵
    public final static Preferences.Key<Boolean> NEED_INIT_PERSONAL_INFO = PreferencesKeys.booleanKey("need_init_personal_info");
    public final static Preferences.Key<String> NAME_KEY = PreferencesKeys.stringKey("name");
    public final static Preferences.Key<String> PARENT_NAME_KEY = PreferencesKeys.stringKey("parent_name");
    public final static Preferences.Key<String> PARENT_RELATION_KEY = PreferencesKeys.stringKey("parent_relation");
    public final static Preferences.Key<Integer> SEX_KEY = PreferencesKeys.intKey("sex");
    public final static Preferences.Key<String> BIRTHDAY_KEY = PreferencesKeys.stringKey("birthday");
    public final static Preferences.Key<String> SCHOOL_KEY = PreferencesKeys.stringKey("school");
    public final static Preferences.Key<Integer> GRADE_KEY = PreferencesKeys.intKey("grade");
    public final static Preferences.Key<String> HEIGHT_KEY = PreferencesKeys.stringKey("height");
    public final static Preferences.Key<String> WEIGHT_KEY = PreferencesKeys.stringKey("weight");
    public final static Preferences.Key<Integer> HANDEDNESS_KEY = PreferencesKeys.intKey("handedness");
    public final static Preferences.Key<Integer> FOOTEDNESS_KEY = PreferencesKeys.intKey("footedness");
    public final static Preferences.Key<String> PHONEMODEL_KEY = PreferencesKeys.stringKey("phonemodel");

    // 兒童同意書、受試者同意書
    public final static Preferences.Key<Boolean> CHILD_CONSENT_SIGNED = PreferencesKeys.booleanKey("child_consent_signed");
    public final static Preferences.Key<Boolean> PARTICIPANT_CONSENT_SIGNED = PreferencesKeys.booleanKey("participant_consent_signed");

    private static RxDataStore<Preferences> dataStore;

    public MyDataStore(Context context) {
        if (dataStore == null) {
            dataStore = new RxPreferenceDataStoreBuilder(context, /*name=*/ FILE_NAME).build();
        }
    };

    // 寫入數據
    public <T> void putValue(Preferences.Key<T> key, T value) {
        dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.set(key, value);
            Log.e("DataStore", "寫入數據__" + key + "->" + value);
            return Single.just(mutablePreferences);
        });
    }

    // 獲取數據
    public <T> T getValue(Preferences.Key<T> key) {
        Flowable<T> value = dataStore.data().map(preferences -> preferences.get(key));
        return value.blockingFirst();
    }
}
