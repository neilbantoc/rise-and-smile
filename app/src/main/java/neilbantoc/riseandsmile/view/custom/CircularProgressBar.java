package neilbantoc.riseandsmile.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import neilbantoc.riseandsmile.R;

/**
 * Created by neilbantoc on 17/11/2016.
 */

public class CircularProgressBar extends View {

    private static final float DEFAULT_STROKE_WIDTH = 6;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    private static final float DEFAULT_BACKGROUND_ALPHA = 0.3f;
    private static final int DEFAULT_FOREGROUND_COLOR = Color.parseColor("#FF4081");

    private float mStrokeWidth;
    private float mProgress;
    private float mMax;
    private float mStartAngle;

    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;

    private RectF mRectF;

    private int mForegroundColor;

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mStrokeWidth = DEFAULT_STROKE_WIDTH;
        mProgress = 0;
        mMax = 1;
        mStartAngle = -90;
        mForegroundColor = DEFAULT_FOREGROUND_COLOR;

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0);
            try {
                mStrokeWidth = typedArray.getDimension(R.styleable.CircleProgressBar_progressBarThickness, mStrokeWidth);
                mProgress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, mProgress);
                mForegroundColor = typedArray.getInt(R.styleable.CircleProgressBar_progressBarColor, mForegroundColor);
                mMax = typedArray.getFloat(R.styleable.CircleProgressBar_max, mMax);
            } finally {
                typedArray.recycle();
            }
        }

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(adjustAlpha(DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_ALPHA));
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(mStrokeWidth);

        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint.setColor(mForegroundColor);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeWidth(mStrokeWidth);

        mRectF = new RectF();
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        // determine smallest of the two dimensions
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);

        // create a square rectf using the smallest dimension that will determine the  drawable area
        mRectF.set(0 + mStrokeWidth / 2, 0 + mStrokeWidth / 2, min - mStrokeWidth / 2, min - mStrokeWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(mRectF, mBackgroundPaint);
        float angle = 360 * mProgress / mMax;
        canvas.drawArc(mRectF, mStartAngle, angle, false, mForegroundPaint);
    }

    public float getMax() {
        return mMax;
    }

    public void setMax(float max) {
        mMax = max;
        invalidate();
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        mStartAngle = startAngle;
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        invalidate();
    }
}
