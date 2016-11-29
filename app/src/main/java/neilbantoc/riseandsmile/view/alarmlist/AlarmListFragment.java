package neilbantoc.riseandsmile.view.alarmlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neilbantoc.riseandsmile.App;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.contract.alarm.AlarmList;
import neilbantoc.riseandsmile.model.Alarm;
import neilbantoc.riseandsmile.presenter.alarmlist.AlarmListPresenter;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public class AlarmListFragment extends Fragment implements AlarmList.View{
    private static final String TAG = AlarmListFragment.class.getSimpleName();

    private AlarmListPresenter mAlarmListPresenter;

    private AlarmListAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private Alarm mCurrentlyOpenedAlarm;

    @BindView(R.id.alarm_list)
    RecyclerView mRecyclerView;

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

        if (mAdapter == null) {
            mAdapter = new AlarmListAdapter();
            mLayoutManager = new LinearLayoutManager(getActivity());
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    public void createNewAlarm() {
        mAlarmListPresenter.onAddAlarmClick();
    }

    @Override
    public void showAlarmDetail(Alarm alarm) {
        onSaveAlarmClick(alarm);
    }

    @Override
    public void showAlarmList(List<Alarm> alarms) {
        mAdapter.addAll(alarms);
    }

    @Override
    public void clearAlarmList() {
        mAdapter.clearAlarms();
    }

    public void onSaveAlarmClick(Alarm alarm) {
        alarm.setActive(true);
        alarm.setTime(System.currentTimeMillis() + 1000);
        mAlarmListPresenter.onSaveAlarmClick(alarm);
    }
}
