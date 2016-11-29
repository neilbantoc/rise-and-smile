package neilbantoc.riseandsmile.contract;

import java.util.List;

import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public interface AlarmList {
    interface View {
        void showAddAlarmForm(Alarm draftAlarm);
        void showAlarmList(List<Alarm> alarms);
        void clearAlarmList();
    }

    interface UserActionCallback {
        void setAlarm(Alarm alarm);
        void deleteAlarm(Alarm alarm);
        void onShow();
        void onAddAlarmClick();
    }
}
