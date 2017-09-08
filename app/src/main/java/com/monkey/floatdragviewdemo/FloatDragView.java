package com.monkey.floatdragviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 可悬浮的、可拖动的view
 */
public class FloatDragView extends View {

    private int mRadius = 75;
    private Paint mTextPaint;
    private Paint mBitmapPaint;
    private String mText = "正式";
    private Bitmap bitmap;
    private OnScrollListener mOnScrollListener;
    private OnClickListener mOnClickListener;

    public FloatDragView(Context context) {
        this(context, null);
    }

    public FloatDragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.parseColor("#05C4CE"));
        mTextPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.medium_text_size));
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.float_drag_bg);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mRadius * 2, mRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, mBitmapPaint);
        float textWidth = mTextPaint.measureText(mText, 0, mText.length());
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(mText, 0, mText.length(), mRadius - textWidth / 2, mRadius +
                -(fontMetrics.ascent + fontMetrics.descent) / 2, mTextPaint);
    }

    private float mTouchX;
    private float mTouchY;
    private float mStartX;
    private float mStartY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY() - getStatusBarHeight(getContext());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll((int) (x - mTouchX), (int) (y - mTouchY));
                }
                break;
            case MotionEvent.ACTION_UP:
                mTouchX = mTouchY = 0;
                if (Math.abs(x - mStartX) < 5 && Math.abs(y - mStartY) < 5) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick();
                    }
                }
                break;
        }
        return true;
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(int x, int y);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick();
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
