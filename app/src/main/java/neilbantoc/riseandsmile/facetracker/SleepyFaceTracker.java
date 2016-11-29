package neilbantoc.riseandsmile.facetracker;

import android.graphics.PointF;
import android.graphics.RectF;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.Map;

import neilbantoc.riseandsmile.view.custom.CameraSourcePreview;

/**
 * Created by neilbantoc on 12/11/2016.
 */

public class SleepyFaceTracker extends Tracker<Face> {
    private static final int STATE_MISSING = 0x00;
    private static final int STATE_EYES_CLOSED = 0x01;
    private static final int STATE_ONE_EYE_OPEN = 0x02;
    private static final int STATE_EYES_OPEN = 0x03;

    private static final float EYE_CLOSED_THRESHOLD = 0.4f;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    // Similarly, keep track of the previous eye open state so that it can be reused for
    // intermediate frames which lack eye landmarks and corresponding eye state.
    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;

    private int mState;

    private SleepyFaceListener mListener;
    private CameraSourcePreview mPreview;

    public SleepyFaceTracker(SleepyFaceListener listener, CameraSourcePreview preview) {
        mListener = listener;
        mState = STATE_MISSING;
        mPreview = preview;
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        if (mState == STATE_MISSING) {
            return;
        }
        mListener.onFaceStateChange(true);

        mState = STATE_MISSING;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isLeftOpen = mPreviousIsLeftOpen;
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftOpen = isLeftOpen;
        }

        float rightOpenScore = face.getIsRightEyeOpenProbability();
        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isRightOpen = mPreviousIsRightOpen;
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightOpen = isRightOpen;
        }

        int eyesOpen = 2;
        eyesOpen = isLeftOpen ? eyesOpen : eyesOpen - 1;
        eyesOpen = isRightOpen ? eyesOpen : eyesOpen - 1;

        if (mState == STATE_MISSING) {
            mListener.onFaceStateChange(false);
        }

        if (eyesOpen == 2 && mState != STATE_EYES_OPEN) {
            mListener.onEyeStateChange(true, 2);
            mState = STATE_EYES_OPEN;
        } else if (eyesOpen == 1 && mState != STATE_ONE_EYE_OPEN) {
            mListener.onEyeStateChange(false, 1);
            mState = STATE_ONE_EYE_OPEN;
        } else if (eyesOpen == 0 && mState != STATE_EYES_CLOSED) {
            mListener.onEyeStateChange(false, 0);
            mState = STATE_EYES_CLOSED;
        }

        mPreview.setFaceBounds(getFaceBoundingBox(face));
    }

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

    private RectF getFaceBoundingBox(Face face) {
        return new RectF(face.getPosition().x,
                face.getPosition().y,
                face.getPosition().x + face.getWidth(),
                face.getPosition().y + face.getHeight());
    }
}
