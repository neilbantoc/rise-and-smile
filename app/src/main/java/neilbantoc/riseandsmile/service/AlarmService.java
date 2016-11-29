package neilbantoc.riseandsmile.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import neilbantoc.riseandsmile.view.alarm.AlarmActivity;

/**
 * Created by neilbantoc on 14/11/2016.
 */
public class AlarmService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String ACTION_START_ALARM_ACTIVITY = AlarmService.class.getName() + ".startAlarmActivity";
    public static final int PRIORITY_ACTIVITY = 2;
    public static final int PRIORITY_SERVICE = 1;

    private static final String TAG = "AlarmService";
    private static final String ACTION_SET_LEVEL = "level";
    private static final String EXTRA_LEVEL = "level";
    private static final long ALARM_ACTIVITY_STARTUP_INTERVAL = 1 * 1000;

    private boolean mPlayerPrepared;
    private BroadcastReceiver mLaunchAlarmActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Broadcast Received in Service");
            Intent activityIntent = new Intent(context, AlarmActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    };
    private Handler mHandler;
    private Runnable mAlarmActivityStarter = new Runnable() {
        @Override
        public void run() {
            AlarmService.this.sendOrderedBroadcast(new Intent(ACTION_START_ALARM_ACTIVITY), null);
            mHandler.postDelayed(this, ALARM_ACTIVITY_STARTUP_INTERVAL);
        }
    };
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;
    private float mLevel;
    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null || !intent.getAction().equals(ACTION_SET_LEVEL) || intent.getExtras() == null) {
                return;
            }

            mLevel = intent.getFloatExtra(EXTRA_LEVEL, mLevel);
            if (mPlayerPrepared) {
                mPlayer.setVolume(mLevel, mLevel);
            }
        }
    };

    /**
     * PUBLIC CONVENIENCE METHODS
     */
    public static void start(Context context) {
        context.startService(new Intent(context, AlarmService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, AlarmService.class));
    }

    public static void setLevel(Context context, float level) {
        Intent intent = new Intent(ACTION_SET_LEVEL);
        intent.putExtra(EXTRA_LEVEL, level);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prepareMediaPlayer();
        prepareVibrator();
        prepareBroadcastReciever();
        startAlarmActivity();
    }

    private void prepareMediaPlayer() {
        mPlayer = new MediaPlayer();

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        try {
            mPlayer.setDataSource(this, alert);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        // prepare listeners
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);

        // initialize player settings
        mPlayer.setVolume(0.0f, 0.0f);
        mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mPlayer.setLooping(true);

        // start
        mPlayer.prepareAsync();
    }

    private void prepareVibrator() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(new long[]{500, 500}, 0);
    }

    private void prepareBroadcastReciever() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mVolumeReceiver, new IntentFilter(ACTION_SET_LEVEL));

        IntentFilter filter = new IntentFilter(ACTION_START_ALARM_ACTIVITY);
        filter.setPriority(PRIORITY_SERVICE);
        registerReceiver(mLaunchAlarmActivityReceiver, filter);
    }

    private void startAlarmActivity() {
        mHandler = new Handler();
        mHandler.post(mAlarmActivityStarter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mVolumeReceiver);
        unregisterReceiver(mLaunchAlarmActivityReceiver);
        mPlayerPrepared = false;
        if (mPlayer != null) {
            mPlayer.release();
        }

        mVibrator.cancel();
        mHandler.removeCallbacks(mAlarmActivityStarter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mPlayer == null) {
            return;
        }

        mPlayerPrepared = true;
        mPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.e("AlarmService", "Media Player Error: " + i + "," + i1);
        stopSelf();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
