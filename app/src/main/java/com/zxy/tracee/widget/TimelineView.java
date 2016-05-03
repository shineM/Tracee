package com.zxy.tracee.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zxy.tracee.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zxy on 16/3/18.
 * 自定义时间线View
 */
public class TimelineView extends View {

    private static final int DEFAULT_TEXT_MARGIN_TOP = 40;
    private static final int DEFAULT_TEXT_SIZE = 10;
    private boolean isVisible = true;

    private static final int TOP_LINE_SIZE = 24;

    private static final int DEVIDE_CIRCLE_RADIUS = 30;

    private static final int LINE_STROKE = 5;

    private int mDevideColor;

    private int mTopLine;

    private Paint mPaint;

    private Paint mLinePaint;

    private RectF mRect;

    private int paddingLeft;

    private int paddingRight;

    private int paddingTop;

    private int textMarginTop;

    private int paddingBottom;

    private boolean isFirstItem = false;

    private boolean isLastItem = false;

    private int mPosition = 2;

    private int textSize;

    public TimelineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimelineView(Context context, AttributeSet attrs) {

        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimelineView, 0, 0);
        try {
            this.mDevideColor = typedArray.getColor(R.styleable.TimelineView_devidePic, Color.GREEN);
            this.mTopLine = typedArray.getDimensionPixelSize(R.styleable.TimelineView_topLine, TOP_LINE_SIZE);
            this.paddingLeft = getPaddingLeft();
            this.paddingRight = getPaddingRight();
            this.paddingTop = getPaddingTop();
            this.paddingBottom = getPaddingBottom();
            this.textSize = typedArray.getDimensionPixelSize(R.styleable.TimelineView_textSize, DEFAULT_TEXT_SIZE);
            this.textMarginTop = typedArray.getDimensionPixelSize(R.styleable.TimelineView_textMarginTop, DEFAULT_TEXT_MARGIN_TOP);

        } finally {
            typedArray.recycle();
        }

        mPaint = new Paint();
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(LINE_STROKE);
        mLinePaint.setColor(Color.rgb(169, 169, 169));
    }


    private int getColor(int mPosition) {

        int colorPurple = Color.rgb(106, 90, 205);
        int colorBlue = Color.rgb(65, 105, 225);
        int colorGreen = Color.rgb(95, 158, 160);
        int colorOrange = Color.rgb(255, 127, 80);
        int colorBear = Color.rgb(255, 228, 181);

        List<Integer> colors = new ArrayList();
        colors.add(colorBlue);
        colors.add(colorPurple);
        colors.add(colorGreen);
        colors.add(colorOrange);
        colors.add(colorBear);

        return colors.get(mPosition);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        mRect = new RectF(getLeft(), getTop(), getRight(), getBottom());

        //do something here
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(getColor(mPosition - 2));
        if (isFirstItem) {
            if (isVisible) {
                canvas.drawCircle(paddingLeft, paddingTop + mTopLine, DEVIDE_CIRCLE_RADIUS, mPaint);
                canvas.drawLine(paddingLeft, paddingTop + mTopLine + DEVIDE_CIRCLE_RADIUS, paddingLeft, paddingTop + 2 * mTopLine, mLinePaint);
            } else {
                canvas.drawCircle(paddingLeft, paddingTop + mTopLine, DEVIDE_CIRCLE_RADIUS, mPaint);

            }
        } else if (isLastItem) {
            canvas.drawLine(paddingLeft, paddingTop, paddingLeft, paddingTop + textMarginTop, mLinePaint);
            canvas.drawLine(paddingLeft, paddingTop + textMarginTop + textSize -5, paddingLeft, paddingTop + mTopLine, mLinePaint);

            canvas.drawCircle(paddingLeft, paddingTop + mTopLine, DEVIDE_CIRCLE_RADIUS, mPaint);
        } else {
            canvas.drawLine(paddingLeft, paddingTop, paddingLeft, paddingTop + textMarginTop, mLinePaint);
            canvas.drawLine(paddingLeft, paddingTop + textMarginTop + textSize-5, paddingLeft, paddingTop + mTopLine, mLinePaint);
            canvas.drawCircle(paddingLeft, paddingTop + mTopLine, DEVIDE_CIRCLE_RADIUS, mPaint);
            canvas.drawLine(paddingLeft, paddingTop + mTopLine + DEVIDE_CIRCLE_RADIUS, paddingLeft, paddingTop + 2 * mTopLine, mLinePaint);
        }

    }

    public void setIsFirstItem(boolean isFirstItem) {
        this.isFirstItem = isFirstItem;
    }

    public void setIsLastItem(boolean isLastItem) {
        this.isLastItem = isLastItem;
    }

    public void setmPosition(int position) {
        this.mPosition = position;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
