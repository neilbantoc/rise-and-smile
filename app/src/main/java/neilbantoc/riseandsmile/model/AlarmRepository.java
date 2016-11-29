package neilbantoc.riseandsmile.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import neilbantoc.riseandsmile.App;
import neilbantoc.riseandsmile.contract.repository.IAlarmRepository;
import neilbantoc.riseandsmile.view.alarm.AlarmActivity;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public class AlarmRepository implements IAlarmRepository{
    private static final String TAG = AlarmRepository.class.getSimpleName();

    private static final int REQUEST_CODE = 0x01;

    private PendingIntent mPendingIntent;
    private AlarmManager mManager;
    private Realm mRealm;

    public AlarmRepository(Context context) {
        mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, AlarmActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mRealm = App.getRealm();
    }

    @Override
    public void armAlarm(Alarm alarm) {
        if (Build.VERSION.SDK_INT > 18) {
            mManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), mPendingIntent);
        } else {
            mManager.set(AlarmManager.RTC_WAKEUP, alarm.getTime(), mPendingIntent);
        }
    }

    @Override
    public void disarmAlarm(Alarm alarm) {
        mManager.cancel(mPendingIntent);
    }

    @Override
    public void createAlarm(Alarm alarm) {
        Number maxId = mRealm.where(Alarm.class).max("mId");
        alarm.setId(maxId == null ? 0 : (maxId.longValue() + 1));
        updateAlarm(alarm);
    }

    @Override
    public void updateAlarm(Alarm alarm) {
        mRealm.beginTransaction();
        mRealm.insertOrUpdate(alarm);
        mRealm.commitTransaction();
        Log.e(TAG, "updateAlarm: Size: " + mRealm.where(Alarm.class).findAll().size());
    }

    @Override
    public void deleteAlarm(Alarm alarm) {
        alarm.deleteFromRealm();
    }

    @Override
    public Alarm getAlarm(long alarmId) {
        return null;
    }

    @Override
    public List<Alarm> getAllAlarms() {
        return new ArrayList<>(mRealm.where(Alarm.class).findAll());
    }
}
