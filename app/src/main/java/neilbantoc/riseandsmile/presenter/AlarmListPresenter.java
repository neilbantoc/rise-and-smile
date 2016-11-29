package neilbantoc.riseandsmile.presenter;

import neilbantoc.riseandsmile.contract.AlarmList;
import neilbantoc.riseandsmile.contract.IAlarmRepository;
import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public class AlarmListPresenter implements AlarmList.UserActionCallback{

    private AlarmList.View mView;
    private IAlarmRepository mRepository;

    private Alarm mDraftAlarm;

    public AlarmListPresenter(AlarmList.View view, IAlarmRepository repository) {
        mView = view;
        mRepository = repository;
        mDraftAlarm = new Alarm();
    }

    @Override
    public void setAlarm(Alarm alarm) {
        if (alarm.isActive()) {
            mRepository.setAlarm(alarm);
        } else {
            mRepository.updateAlarm(alarm);
        }
    }

    @Override
    public void deleteAlarm(Alarm alarm) {
        mRepository.deleteAlarm(alarm);
    }

    @Override
    public void onShow() {
        // clear and refresh alarm list
    }

    @Override
    public void onAddAlarmClick() {
        mView.showAddAlarmForm(mDraftAlarm);
    }

    public Alarm getDraftAlarm() {
        return mDraftAlarm;
    }
}
