package neilbantoc.riseandsmile;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

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
        sAlarmRepository = new AlarmRepository(this);
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

    public static AlarmRepository getAlarmRepository() {
        return sAlarmRepository;
    }
}
