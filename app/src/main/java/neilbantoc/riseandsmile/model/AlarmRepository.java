package neilbantoc.riseandsmile.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.List;

import io.realm.Realm;
import neilbantoc.riseandsmile.App;
import neilbantoc.riseandsmile.contract.repository.IAlarmRepository;
import neilbantoc.riseandsmile.view.alarm.AlarmActivity;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public class AlarmRepository implements IAlarmRepository{
    public static final String EXTRA_ALARM_ID = "alarm_id";
    private static final String TAG = AlarmRepository.class.getSimpleName();

    private static final int REQUEST_CODE = 0x01;

    private PendingIntent mPendingIntent;
    private AlarmManager mManager;
    private Realm mRealm;

    private Context mContext;

    public AlarmRepository(Context context) {
        mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mRealm = App.getRealm();
        mContext = context;
    }

    private PendingIntent updatePendingIntent(Alarm alarm) {
        Intent intent = new Intent(mContext, AlarmActivity.class);
        intent.putExtra(EXTRA_ALARM_ID, alarm.getId());
        mPendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }

    @Override
    public void armAlarm(Alarm alarm) {
        if (Build.VERSION.SDK_INT > 18) {
            mManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), updatePendingIntent(alarm));
        } else {
            mManager.set(AlarmManager.RTC_WAKEUP, alarm.getTime(), updatePendingIntent(alarm));
        }
    }

    @Override
    public void disarmAlarm(Alarm alarm) {
        mManager.cancel(updatePendingIntent(alarm));
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
    }

    @Override
    public void deleteAlarm(Alarm alarm) {
        mRealm.beginTransaction();
        mRealm.where(Alarm.class).equalTo("mId", alarm.getId()).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    @Override
    public Alarm getAlarm(long alarmId) {
        return mRealm.copyFromRealm(mRealm.where(Alarm.class).equalTo("mId", alarmId).findFirst());
    }

    @Override
    public List<Alarm> getAllAlarms() {
        return mRealm.copyFromRealm(mRealm.where(Alarm.class).findAll());
    }
}
