package neilbantoc.riseandsmile;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import neilbantoc.riseandsmile.model.AlarmRepository;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public class App extends MultiDexApplication{

    private static AlarmRepository sAlarmRepository;

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        sAlarmRepository = new AlarmRepository(this);
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

    public static AlarmRepository getAlarmRepository() {
        return sAlarmRepository;
    }

    public static Realm getRealm() {
        return Realm.getDefaultInstance();
    }
}
