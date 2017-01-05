package neilbantoc.riseandsmile.presenter.alarmlist;

import neilbantoc.riseandsmile.contract.alarm.AlarmList;
import neilbantoc.riseandsmile.contract.repository.IAlarmRepository;
import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public class AlarmListPresenter implements AlarmList.UserActionCallback{
    private static final String TAG = AlarmListPresenter.class.getSimpleName();

    private AlarmList.View mView;
    private IAlarmRepository mRepository;

    private Alarm mDraftAlarm;

    public AlarmListPresenter(AlarmList.View view, IAlarmRepository repository) {
        mView = view;
        mRepository = repository;
        createNewDraftAlarm();
    }

    private void createNewDraftAlarm() {
        mDraftAlarm = new Alarm();
    }

    @Override
    public void onShow() {
        // clear and refresh alarm list
        mView.clearAlarmList();
        mView.showAlarmList(mRepository.getAllAlarms());
    }

    @Override
    public void onAlarmClick(Alarm alarm) {
        mView.showAlarmDetail(alarm);
    }

    @Override
    public void onAddAlarmClick() {
        mDraftAlarm.setTime(System.currentTimeMillis());
        mView.showAlarmDetail(mDraftAlarm);
    }

    @Override
    public void onSaveAlarmClick(Alarm alarm) {
        alarm.setTimeRelativeToNow();

        armAlarmIfActive(alarm);

        if (alarm.equals(mDraftAlarm)) {
            mRepository.createAlarm(alarm);
            createNewDraftAlarm();
            refreshList(true);
        } else {
            mRepository.updateAlarm(alarm);
            refreshList(false);
        }
    }

    @Override
    public void onDeleteAlarmClick(Alarm alarm) {
        mRepository.deleteAlarm(alarm);
        mRepository.disarmAlarm(alarm);
        refreshList(true);
    }

    private void refreshList(boolean reload) {
        if (reload) {
            mView.clearAlarmList();
            mView.showAlarmList(mRepository.getAllAlarms());
        } else {
            mView.refreshAlarmList();
        }
    }

    private void armAlarmIfActive(Alarm alarm) {
        if (alarm.isActive()) {
            mRepository.armAlarm(alarm);
            mView.showNextAlarmTime(alarm);
        } else {
            mRepository.disarmAlarm(alarm);
        }
    }

    @Override
    public void onToggleAlarmClick(Alarm alarm) {
        alarm.setActive(!alarm.isActive());
        alarm.setTimeRelativeToNow();
        mRepository.updateAlarm(alarm);
        armAlarmIfActive(alarm);
        refreshList(false);
    }
}
