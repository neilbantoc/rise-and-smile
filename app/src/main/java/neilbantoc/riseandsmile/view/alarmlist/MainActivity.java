package neilbantoc.riseandsmile.view.alarmlist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.view.alarm.AlarmActivity;

/**
 * Created by neilbantoc on 17/11/2016.
 */

public class MainActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener {

    @BindView(R.id.sliding_up_panel_layout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;

    @BindView(R.id.fab_add_alarm)
    FloatingActionButton mFabAlarm;

    AlarmListFragment mAlarmListFragment;

    @BindString(R.string.tag_alarms_list)
    String TAG_ALARM_LIST;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);

        mSlidingUpPanelLayout.addPanelSlideListener(this);

        mAlarmListFragment = (AlarmListFragment) getSupportFragmentManager().findFragmentByTag(TAG_ALARM_LIST);
    }

    @OnClick(R.id.fab_add_alarm)
    public void onFabClick() {
        if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            AlarmActivity.showTutorial(this);
        } else {
            mAlarmListFragment.createNewAlarm();
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        int resId = newState == SlidingUpPanelLayout.PanelState.COLLAPSED ? R.drawable.ic_help_outline_white_24dp : R.drawable.ic_add_white_24dp;
        mFabAlarm.setImageResource(resId);
    }
}
