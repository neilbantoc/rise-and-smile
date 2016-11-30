package neilbantoc.riseandsmile.view.alarmlist;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.model.Alarm;

/**
 * Created by neilbantoc on 29/11/2016.
 */

public class AlarmDetailFragment extends DialogFragment {

    @BindView(R.id.time_picker)
    TimePicker mTimePicker;

    private DialogInterface.OnClickListener mListener;

    private Alarm mAlarm;

    public void setAlarm(Alarm alarm) {
        mAlarm = alarm;
    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_alarm_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAlarm != null && mTimePicker != null) {
            mTimePicker.setCurrentHour(mAlarm.getHour());
            mTimePicker.setCurrentMinute(mAlarm.getMinute());
        }
    }

    @OnClick(R.id.save)
    public void onSave() {
        mAlarm.setTimeRelativeToNow(mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
        if (mListener != null) {
            mListener.onClick(null, DialogInterface.BUTTON_POSITIVE);
        }
        dismiss();
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        if (mListener != null) {
            mListener.onClick(null, DialogInterface.BUTTON_NEGATIVE);
        }
        dismiss();
    }

    @OnClick(R.id.delete)
    public void onDelete() {
        if (mListener != null) {
            mListener.onClick(null, DialogInterface.BUTTON_NEUTRAL);
        }
        dismiss();
    }


}
