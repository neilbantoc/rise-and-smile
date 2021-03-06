package neilbantoc.riseandsmile.contract.repository;

import java.util.List;

import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 23/11/2016.
 */

public interface IAlarmRepository {
    void armAlarm(Alarm alarm);
    void disarmAlarm(Alarm alarm);
    void createAlarm(Alarm alarm);
    void updateAlarm(Alarm alarm);
    void deleteAlarm(Alarm alarm);
    void deactivatePastAlarms();
    Alarm getAlarm(long alarmId);
    List<Alarm> getAllAlarms();
}
