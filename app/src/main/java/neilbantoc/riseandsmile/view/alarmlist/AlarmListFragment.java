package neilbantoc.riseandsmile.view.alarmlist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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

public class AlarmListFragment extends Fragment implements AlarmList.View, DialogInterface.OnClickListener, AlarmListAdapter.OnAlarmClickListener {
    private static final String TAG = AlarmListFragment.class.getSimpleName();

    private AlarmListPresenter mAlarmListPresenter;

    private AlarmListAdapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private AlarmDetailFragment mAlarmDetailFragment;

    @BindView(R.id.alarm_list)
    RecyclerView mRecyclerView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAlarmListPresenter = new AlarmListPresenter(this, App.getAlarmRepository());
        mAlarmDetailFragment = new AlarmDetailFragment();
        mAlarmDetailFragment.setOnClickListener(this);
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
            mAdapter.setOnAlarmClickListener(this);
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
        mAlarmDetailFragment.setAlarm(alarm);
        mAlarmDetailFragment.show(getChildFragmentManager(), "detail");
    }

    @Override
    public void showAlarmList(List<Alarm> alarms) {
        mAdapter.addAll(alarms);
    }

    @Override
    public void clearAlarmList() {
        mAdapter.clearAlarms();
    }

    @Override
    public void refreshAlarmList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNextAlarmTime(Alarm alarm) {
        long now = System.currentTimeMillis();
        now = now - now % Alarm.MINUTE_IN_MILLIS;
        String text = "Next alarm will be " + DateUtils.getRelativeTimeSpanString(alarm.getTime(), now, Alarm.MINUTE_IN_MILLIS);
        Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Alarm alarm = mAlarmDetailFragment.getAlarm();
        if (i == DialogInterface.BUTTON_POSITIVE) {
            alarm.setActive(true);
            mAlarmListPresenter.onSaveAlarmClick(alarm);
        } else if (i == DialogInterface.BUTTON_NEUTRAL) {
            mAlarmListPresenter.onDeleteAlarmClick(alarm);
        }
    }

    @Override
    public void onAlarmClick(Alarm alarm) {
        mAlarmListPresenter.onAlarmClick(alarm);
    }

    @Override
    public void onToggleAlarmClick(Alarm alarm) {
        mAlarmListPresenter.onToggleAlarmClick(alarm);
    }
}
