package neilbantoc.riseandsmile.presenter;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import neilbantoc.riseandsmile.contract.AlarmScreen;
import neilbantoc.riseandsmile.facetracker.SleepyFaceListener;

/**
 * Created by neilbantoc on 16/11/2016.
 */

public class AlarmActivityPresenter implements SleepyFaceListener, AlarmScreen.UserActionsCallback{
    private static final String TAG = AlarmActivityPresenter.class.getSimpleName();
    private static final int STATE_MISSING = 0x00;
    private static final int STATE_AWAKE = 0x01;
    private static final int STATE_SLEEPING = 0x02;

    private static final long CLOSED_EYES_TIMEOUT = (long) (0.01f * 1000); // threshold for determining a blink vs dozing off
    private static final long AWAKE_ANIMATION_DURATION = 20 * 1000; // number of seconds the user must stay awake
    private static final long VOLUME_ANIMATION_DURATION = 4 * 1000; // how long the volume animates from ringing to silent
    private static final long RESET_ANIMATION_DURATION = 8 * 1000; // how long both the awake progress and the volume progress animate back to 100% from its current value
    private static final int RESET_VOLUME_SCALE = 4; // how faster the volume animates back to 100% compared to the awake progress when resetting

    private AlarmScreen.View mView;

    private Handler mHandler;

    private float mAwakeProgress = 1.0f;
    private float mVolumeProgress = 1.0f;
    private float mResetProgress = 0.0f;

    private int mState = STATE_MISSING;

    private ValueAnimator mAwakeAnimator;
    private ValueAnimator mVolumeDownAnimator;
    private ValueAnimator mResetAnimator;

    private boolean mEyesOpen;
    private boolean mIsCountingDown;

    private Runnable mClosedEyeTimer = new Runnable() {
        @Override
        public void run() {
            if (mEyesOpen && mState != STATE_AWAKE) {
                Log.d(TAG, "=======");
                Log.d(TAG, "STATE CHANGE: AWAKE");
                startAwakeAnimator();
                mState = STATE_AWAKE;
            } else if (!mEyesOpen && mState != STATE_SLEEPING) {
                Log.d(TAG, "=======");
                Log.d(TAG, "STATE CHANGE: SLEEPING");
                startResetAnimator();
                mState = STATE_SLEEPING;
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener mAwakeUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (!mIsCountingDown) {
                return;
            }

            updateAwakeLevel((float) valueAnimator.getAnimatedValue());
        }
    };
    private ValueAnimator.AnimatorUpdateListener mVolumeUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (!mIsCountingDown) {
                return;
            }

            updateVolumeLevel((float) valueAnimator.getAnimatedValue());
        }
    };
    private ValueAnimator.AnimatorUpdateListener mResetUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (mIsCountingDown) {
                return;
            }

            float increment = ((float) valueAnimator.getAnimatedValue()) - mResetProgress;
            mResetProgress = (float) valueAnimator.getAnimatedValue();
            updateAwakeLevel(Math.min(mAwakeProgress + increment, 1.0f));
            updateVolumeLevel(Math.min(mVolumeProgress + (increment * RESET_VOLUME_SCALE), 1.0f));
        }
    };

    public AlarmActivityPresenter(AlarmScreen.View view) {
        mHandler = new Handler();
        mView = view;

        mAwakeAnimator = ValueAnimator.ofFloat(1, 0);
        mAwakeAnimator.setInterpolator(new LinearInterpolator());
        mAwakeAnimator.setDuration(AWAKE_ANIMATION_DURATION);
        mAwakeAnimator.addUpdateListener(mAwakeUpdateListener);

        mVolumeDownAnimator = ValueAnimator.ofFloat(1, 0);
        mVolumeDownAnimator.setInterpolator(new LinearInterpolator());
        mVolumeDownAnimator.setDuration(VOLUME_ANIMATION_DURATION);
        mVolumeDownAnimator.addUpdateListener(mVolumeUpdateListener);

        mResetAnimator = ValueAnimator.ofFloat(0, 1);
        mResetAnimator.setInterpolator(new LinearInterpolator());
        mResetAnimator.setDuration(RESET_ANIMATION_DURATION);
        mResetAnimator.addUpdateListener(mResetUpdateListener);

        float initialLevel = 1.0f;
        updateAwakeLevel(initialLevel);
        updateVolumeLevel(initialLevel);
    }

    @Override
    public void onEyeStateChange(final boolean open, int openEyeCount) {
        mEyesOpen = open;

        mHandler.removeCallbacks(mClosedEyeTimer);
        Log.d(TAG, "=======");
        Log.d(TAG, (open ? "OPEN" : "CLOSED") + " awakeProgress: " + mAwakeProgress + " volumeProgress: " + mVolumeProgress);
        if (open) {
            mHandler.postDelayed(mClosedEyeTimer, CLOSED_EYES_TIMEOUT);
        } else {
            mHandler.postDelayed(mClosedEyeTimer, CLOSED_EYES_TIMEOUT);
        }
    }

    @Override
    public void onFaceStateChange(final boolean missing) {
        Log.d(TAG, "=======");
        Log.d(TAG, (missing ? "missing" : "present"));
        if (missing) {
            mState = STATE_MISSING;
            startResetAnimator();
        }
    }

    private void startAwakeAnimator() {
        Log.d(TAG, "=======");
        Log.d(TAG, "Awake Animator: Volume Progress: " + mVolumeProgress + " Awake Progress: " + mAwakeProgress);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsCountingDown = true;
                resetAnimators();

                mAwakeAnimator.setCurrentPlayTime((long) (AWAKE_ANIMATION_DURATION * (1 - mAwakeProgress)));
                mVolumeDownAnimator.setCurrentPlayTime((long) (VOLUME_ANIMATION_DURATION * (1 - mVolumeProgress)));

                mAwakeAnimator.start();
                mVolumeDownAnimator.start();
            }
        });
    }

    private void startResetAnimator() {
        Log.d(TAG, "=======");
        Log.d(TAG, "Reset Animator: Volume Progress: " + mVolumeProgress + " Awake Progress: " + mAwakeProgress);

        mResetProgress = 0;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsCountingDown = false;
                resetAnimators();
                mResetAnimator.start();
            }
        });
    }

    private void resetAnimators() {
        mResetAnimator.cancel();
        mAwakeAnimator.cancel();
        mVolumeDownAnimator.cancel();
    }

    private void updateAwakeLevel(float value) {
        mAwakeProgress = value;
        mView.showAwakeLevel(value);
        if (value == 0) {
            mView.closeAlarmScreen();
        }
    }

    private void updateVolumeLevel(float value) {
        mVolumeProgress = value;
        mView.showVolumeLevel(value);
        mView.changeVolume(value);
    }
}
