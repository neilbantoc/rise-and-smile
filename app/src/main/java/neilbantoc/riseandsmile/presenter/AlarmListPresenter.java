package neilbantoc.riseandsmile.presenter;

import android.util.Log;

import neilbantoc.riseandsmile.contract.AlarmList;
import neilbantoc.riseandsmile.contract.IAlarmRepository;
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
        Log.d(TAG, "onShow: NumAlarms: " + mRepository.getAllAlarms().size());
    }

    @Override
    public void onAlarmClick(Alarm alarm) {
        mView.showAlarmDetail(alarm);
    }

    @Override
    public void onAddAlarmClick() {
        mView.showAlarmDetail(mDraftAlarm);
    }

    @Override
    public void onSaveAlarmClick(Alarm alarm) {
        if (alarm.isActive()) {
            mRepository.armAlarm(alarm);
        }

        if (alarm.equals(mDraftAlarm)) {
            mRepository.createAlarm(alarm);
            createNewDraftAlarm();
        } else {
            mRepository.updateAlarm(alarm);
        }
    }
}
