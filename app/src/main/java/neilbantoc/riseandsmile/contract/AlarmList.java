package neilbantoc.riseandsmile.contract;

import java.util.List;

import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public interface AlarmList {
    interface View {
        void showAlarmDetail(Alarm alarmDetail);
        void showAlarmList(List<Alarm> alarms);
        void clearAlarmList();
    }

    interface UserActionCallback {
        void onShow();
        void onAlarmClick(Alarm alarm);
        void onAddAlarmClick();
        void onSaveAlarmClick(Alarm alarm);
    }
}
