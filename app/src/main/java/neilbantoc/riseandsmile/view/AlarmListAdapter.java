package neilbantoc.riseandsmile.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 28/11/2016.
 */

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private ArrayList<Alarm> mAlarms;

    public AlarmListAdapter () {
        mAlarms = new ArrayList<>();
    }

    public void add(Alarm alarm) {
        mAlarms.add(alarm);
        notifyDataSetChanged();
    }

    public void addAll(Collection<Alarm> alarms) {
        mAlarms.addAll(alarms);
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
        holder.alarmTime.setText(alarm.getTime() + "");
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time)
        TextView alarmTime;
        public AlarmViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
