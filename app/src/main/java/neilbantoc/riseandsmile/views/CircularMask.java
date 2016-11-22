package neilbantoc.riseandsmile.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by neilbantoc on 17/11/2016.
 */

public class CircularMask extends View {

    private RectF mRectF;
    private Paint mPaint;
    private Path mClipPath;
    private PorterDuffXfermode mPorterDuffXfermode;

    public CircularMask(Context context) {
        this(context, null);
    }

    public CircularMask(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRectF = new RectF();
        mClipPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        // determine smallest of the two dimensions
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);

        // create a square rectf using the smallest dimension that will determine the  drawable area
        mRectF.set(0, 0, min, min);
        mClipPath = new Path();
        mClipPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, 255, 255, 255);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, mPaint);
    }
}
