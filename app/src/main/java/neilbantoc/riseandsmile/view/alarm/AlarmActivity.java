/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package neilbantoc.riseandsmile.view.alarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

import neilbantoc.riseandsmile.R;
import neilbantoc.riseandsmile.contract.alarmlist.AlarmScreen;
import neilbantoc.riseandsmile.facetracker.SleepyFaceListener;
import neilbantoc.riseandsmile.facetracker.SleepyFaceTracker;
import neilbantoc.riseandsmile.model.AlarmRepository;
import neilbantoc.riseandsmile.presenter.alarm.AlarmActivityPresenter;
import neilbantoc.riseandsmile.service.AlarmService;
import neilbantoc.riseandsmile.view.BaseActivity;
import neilbantoc.riseandsmile.view.custom.CameraSourcePreview;
import neilbantoc.riseandsmile.view.custom.CircularProgressBar;

public final class AlarmActivity extends BaseActivity implements AlarmScreen.View, View.OnClickListener {
    private static final String EXTRA_IS_TUTORIAL = "extra_tutorial";

    private static final String TAG = "AlarmActivity";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int PROGRESS_BAR_RANGE = 1000;

    private BroadcastReceiver mCancelAlarmActivityLaunchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Broadcast Received in Activity");
            abortBroadcast();
        }
    };

    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private CircularProgressBar mTimeBar;
    private CircularProgressBar mVolumeBar;

    private ImageView mVolumeIcon;
    private ImageView mAwakeIcon;

    private Drawable mEyePresentDrawable;
    private Drawable mEyeMissingDrawable;
    private Drawable mVolumeUp;
    private Drawable mVolumeMid;
    private Drawable mVolumeMute;


    private AlarmActivityPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_alarm);

        mVolumeIcon = (ImageView) findViewById(R.id.icon_volume);
        mAwakeIcon = (ImageView) findViewById(R.id.icon_eye);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mTimeBar = (CircularProgressBar) findViewById(R.id.timeBar);
        mTimeBar.setMax(PROGRESS_BAR_RANGE);

        mVolumeBar = (CircularProgressBar) findViewById(R.id.volumeBar);
        mVolumeBar.setMax(PROGRESS_BAR_RANGE);

        mEyeMissingDrawable = getIcon(R.drawable.ic_visibility_off_black_24dp);
        mEyePresentDrawable = getIcon(R.drawable.ic_visibility_black_24dp);
        mVolumeMute = getIcon(R.drawable.ic_volume_mute_black_24dp);
        mVolumeMid = getIcon(R.drawable.ic_volume_down_black_24dp);
        mVolumeUp = getIcon(R.drawable.ic_volume_up_black_24dp);

        findViewById(R.id.fab_help).setOnClickListener(this);

        mPresenter = new AlarmActivityPresenter(this);

        AlarmService.start(this);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

    }

    private Drawable getIcon(int resId) {
        Drawable icon;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            icon = VectorDrawableCompat.create(getResources(), resId, getTheme());
        } else {
            icon = getResources().getDrawable(resId, getTheme());
        }
        return icon;
    }

    /**
     * Handles the requesting of the camera permission.  This includes showing a "Snackbar" message
     * of why the permission is needed then sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.VIBRATE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(findViewById(R.id.topLayout), R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AlarmService.ACTION_START_ALARM_ACTIVITY);
        filter.setPriority(AlarmService.PRIORITY_ACTIVITY);
        registerReceiver(mCancelAlarmActivityLaunchReceiver, filter);
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 1;
        getWindow().setAttributes(params);
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mCancelAlarmActivityLaunchReceiver);
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        mPresenter.resetAnimators();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on
     * {@link #requestPermissions(String[], int)}.<p>
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction with the user
     * is interrupted. In this case you will receive empty permissions and results arrays which
     * should be treated as a cancellation.<p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public void showAwakeLevel(float value) {
        mTimeBar.setProgress(value * PROGRESS_BAR_RANGE);
        if (mPresenter != null) {
            mAwakeIcon.setImageDrawable(mPresenter.getState() == AlarmActivityPresenter.STATE_AWAKE ? mEyePresentDrawable : mEyeMissingDrawable);
        }
    }

    @Override
    public void changeVolume(float level) {
        AlarmService.setLevel(this, level);
    }

    @Override
    public void closeAlarmScreen() {
        Toast.makeText(this, R.string.alarm_end_prompt, Toast.LENGTH_LONG).show();
        finish();
        AlarmService.stop(this);
    }

    public void showVolumeLevel(float volume) {
        mVolumeBar.setProgress(volume * PROGRESS_BAR_RANGE);

        Drawable drawable = volume > 0.5
                ? mVolumeUp
                : volume > 0
                    ? mVolumeMid
                    : mVolumeMute;

        mVolumeIcon.setImageDrawable(drawable);
    }

    //==============================================================================================
    // Detector
    //==============================================================================================

    /**
     * Creates the face detector and associated processing pipeline to support either front facing
     * mode or rear facing mode.  Checks if the detector is ready to use, and displays a low storage
     * warning if it was not possible to download the face library.
     */
    @NonNull
    private FaceDetector createFaceDetector(Context context) {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(true)
                .setMinFaceSize(0.35f)
                .build();

        Detector.Processor<Face> processor;
        Tracker<Face> tracker = new SleepyFaceTracker((SleepyFaceListener) mPresenter, mPreview);
        processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        return detector;
    }

    //==============================================================================================
    // Camera Source
    //==============================================================================================

    /**
     * Creates the face detector and the camera.
     */
    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;

        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(240, 240)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    @Override
    public void onClick(View view) {
        new AlertDialog.Builder(this).setTitle(R.string.title_keep_eyes_open).setMessage(R.string.message_alarm_activity_help).show();
    }

    public static void showTutorial(Context context) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra(EXTRA_IS_TUTORIAL, true);
        context.startActivity(intent);
    }
}