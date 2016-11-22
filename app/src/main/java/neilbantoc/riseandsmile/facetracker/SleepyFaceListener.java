package neilbantoc.riseandsmile.facetracker;

/**
 * Created by neilbantoc on 14/11/2016.
 */

public interface SleepyFaceListener {
    void onEyeStateChange(boolean open, int openEyeCount);

    void onFaceStateChange(boolean missing);
}
