package neilbantoc.riseandsmile.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import neilbantoc.riseandsmile.contract.IAlarmRepository;
import neilbantoc.riseandsmile.service.AlarmService;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public class AlarmRepository implements IAlarmRepository{

    private static final int REQUEST_CODE = 0x01;

    private PendingIntent mPendingIntent;
    private AlarmManager mManager;

    public AlarmRepository(Context context) {
        mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(context, REQUEST_CODE, new Intent(context, AlarmService.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void setAlarm(Alarm alarm) {
        if (Build.VERSION.SDK_INT > 18) {
            mManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getTime(), mPendingIntent);
        } else {
            mManager.set(AlarmManager.RTC_WAKEUP, alarm.getTime(), mPendingIntent);
        }
    }

    @Override
    public void updateAlarm(Alarm alarm) {
        if (alarm.isActive()) {
            setAlarm(alarm);
        }

    }

    @Override
    public void deleteAlarm(Alarm alarm) {

    }

    private void cancelAlarms() {
        mManager.cancel(mPendingIntent);
    }

    @Override
    public Alarm getAlarm(long alarmId) {
        return null;
    }
}
