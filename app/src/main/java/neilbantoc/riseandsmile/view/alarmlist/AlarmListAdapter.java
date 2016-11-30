package neilbantoc.riseandsmile.view.alarmlist;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 28/11/2016.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    interface OnAlarmClickListener {
        void onAlarmClick(Alarm alarm);
        void onToggleAlarmClick(Alarm alarm);
    }

    private OnAlarmClickListener mListener;

    private ArrayList<Alarm> mAlarms;

    public AlarmListAdapter () {
        mAlarms = new ArrayList<>();
    }

    public void setOnAlarmClickListener(OnAlarmClickListener listener) {
        mListener = listener;
    }

    public void add(Alarm alarm) {
        mAlarms.add(alarm);
        Collections.sort(mAlarms, Alarm.SORTER);
        notifyDataSetChanged();
    }

    public void addAll(Collection<Alarm> alarms) {
        mAlarms.addAll(alarms);
        Collections.sort(mAlarms, Alarm.SORTER);
        notifyDataSetChanged();
    }

    public Alarm getAlarmAt(int index) {
        return mAlarms.get(index);
    }

    public void clearAlarms() {
        mAlarms.clear();
        notifyDataSetChanged();
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlarmViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        Alarm alarm = getAlarmAt(position);
        holder.alarmTime.setText(DateUtils.formatDateTime(holder.alarmTime.getContext(), alarm.getTime(), DateUtils.FORMAT_SHOW_TIME));
        holder.setAlarm(alarm);
        holder.itemView.setActivated(alarm.isActive());
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time)
        TextView alarmTime;

        private Alarm mAlarm;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setAlarm(Alarm alarm) {
            mAlarm = alarm;
        }

        @OnClick(R.id.toggle)
        public void toggle() {
            if (mListener != null) {
                mListener.onToggleAlarmClick(mAlarm);
            }
        }

        @OnClick(R.id.alarm_item)
        public void alarmClick() {
            if (mListener != null) {
                mListener.onAlarmClick(mAlarm);
            }
        }

    }
}
