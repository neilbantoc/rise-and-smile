package neilbantoc.riseandsmile.view;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import neilbantoc.riseandsmile.App;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.contract.AlarmList;
import neilbantoc.riseandsmile.model.Alarm;
import neilbantoc.riseandsmile.model.AlarmRepository;
import neilbantoc.riseandsmile.presenter.AlarmListPresenter;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public class AlarmListFragment extends Fragment implements AlarmList.View{
    private static final String TAG = AlarmListFragment.class.getSimpleName();

    private AlarmListPresenter mAlarmListPresenter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAlarmListPresenter = new AlarmListPresenter(this, App.getAlarmRepository());
    }

    @Override
    public void onResume() {
        super.onResume();
        mAlarmListPresenter.onShow();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_alarm_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void showAddAlarmForm(Alarm draftAlarm) {
        // skip for now and save alarm directly
        onSaveAlarmClick();
    }

    @Override
    public void showAlarmList(List<Alarm> alarms) {

    }

    @Override
    public void clearAlarmList() {

    }

    @OnClick(R.id.fab_add_alarm)
    public void onAddAlarmClick() {
        mAlarmListPresenter.onAddAlarmClick();
    }

    public void onSaveAlarmClick() {
        Log.d(TAG, "onSaveAlarmClick: Saving Alarm");
        Alarm alarm = mAlarmListPresenter.getDraftAlarm();
        alarm.setActive(true);
        alarm.setTime(System.currentTimeMillis() + 10000);
        mAlarmListPresenter.setAlarm(alarm);
    }
}
