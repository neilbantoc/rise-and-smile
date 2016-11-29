package neilbantoc.riseandsmile.contract;

/**
 * Created by neilbantoc on 22/11/2016.
 */

public interface AlarmScreen {
    interface View {
        void showVolumeLevel(float level);
        void showAwakeLevel(float level);
        void changeVolume(float level);
        void closeAlarmScreen();
    }

    interface UserActionsCallback {
        void onEyeStateChange(boolean eyesOpen, int openEyeCount);
        void onFaceStateChange(boolean faceMissing);
    }
}
