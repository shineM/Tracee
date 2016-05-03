package com.zxy.tracee.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.zxy.tracee.R;
import com.zxy.tracee.widget.CornerDialog;

import java.sql.Ref;

/**
 * Created by zxy on 16/4/26.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ReturnToFab extends ChangeBounds {
    private static final String PROPERTY_COLOR = "color";
    private static final String PROPERTY_RADIUS = "radius";
    private static final String[] TRANSITION_PROPERTIES = {
            PROPERTY_COLOR,
            PROPERTY_RADIUS
    };
    private float startRadius;
    private float endRadius;
    private int mColor;

    public ReturnToFab(float startRadius, float endRadius, int color) {
        this.startRadius = startRadius;
        this.endRadius = endRadius;
        this.mColor = color;
    }

    public ReturnToFab() {
        super();
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        transitionValues.values.put(PROPERTY_COLOR, Color.TRANSPARENT);
        transitionValues.values.put(PROPERTY_RADIUS, 2.0f);

    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        super.captureEndValues(transitionValues);
        transitionValues.values.put(PROPERTY_COLOR, ContextCompat.getColor(transitionValues.view.getContext(), R.color.colorPrimary));
        transitionValues.values.put(PROPERTY_RADIUS, (float) transitionValues.view.getWidth() / 2);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || changeBounds == null) {
            return null;
        }
        Integer startColor = (Integer) startValues.values.get(PROPERTY_COLOR);
        Float startRadius = (Float) startValues.values.get(PROPERTY_RADIUS);
        Integer endColor = (Integer) endValues.values.get(PROPERTY_COLOR);
        Float endRadius = (Float) endValues.values.get(PROPERTY_RADIUS);

        CornerDialog drawable = new CornerDialog(startColor, startRadius);
        startValues.view.setBackground(drawable);
        Animator colorAnimator = ObjectAnimator.ofArgb(drawable, drawable.PROPERTY_COLOR, endColor);
        Animator radiusAnimator = ObjectAnimator.ofFloat(drawable, drawable.PROPERTY_RADIUS, endRadius);
        if (endValues.view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) endValues.view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View v = viewGroup.getChildAt(i);
                v.animate().alpha(0f)
                        .translationY(v.getHeight() / 3)
                        .setDuration(50L)
                        .setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(), android.R.interpolator.fast_out_linear_in))
                        .start();

            }
        }


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(), android.R.interpolator.fast_out_slow_in));
        animatorSet.playTogether(changeBounds, colorAnimator, radiusAnimator);
        return animatorSet;
    }

    public void setStartRadius(float startRadius) {
        this.startRadius = startRadius;
    }

    public void setEndRadius(float endRadius) {
        this.endRadius = endRadius;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }
}
