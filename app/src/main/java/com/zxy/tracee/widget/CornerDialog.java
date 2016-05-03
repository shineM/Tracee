package com.zxy.tracee.widget;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;

/**
 * Created by zxy on 16/4/26.
 */
public class CornerDialog extends Drawable {

    public static final Property<CornerDialog, Integer> PROPERTY_COLOR = new Property<CornerDialog, Integer>(Integer.class, "color") {
        @Override
        public Integer get(CornerDialog object) {
            return object.getColor();
        }

        @Override
        public void set(CornerDialog object, Integer value) {
            object.setColor(value);
        }
    };
    public static final Property<CornerDialog, Float> PROPERTY_RADIUS = new Property<CornerDialog, Float>(Float.class, "radius") {
        @Override
        public Float get(CornerDialog object) {
            return object.getRadius();
        }

        @Override
        public void set(CornerDialog object, Float value) {
            object.setRadius(value);
        }
    };
    private int color;

    private float radius;

    private Paint paint;

    public CornerDialog(int color, float radius) {
        this.color = color;
        this.radius = radius;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, radius, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
    }
}
