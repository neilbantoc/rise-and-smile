package neilbantoc.riseandsmile.contract;

import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public interface IAlarmRepository {
    void setAlarm(Alarm alarm);
    void updateAlarm(Alarm alarm);
    void deleteAlarm(Alarm alarm);
    Alarm getAlarm(long alarmId);
}
