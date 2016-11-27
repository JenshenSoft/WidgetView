package com.jenshen.widgetview.util;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jenshen.widgetview.entity.WidgetMotionInfo;
import com.jenshen.widgetview.entity.WidgetPosition;
import com.jenshen.widgetview.listener.OnWidgetMoveUpListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.jenshen.widgetview.util.WidgetSwipeManager.MotionType.LEFT_BOTTOM_ANGLE_MOTION;
import static com.jenshen.widgetview.util.WidgetSwipeManager.MotionType.LEFT_TOP_ANGLE_MOTION;
import static com.jenshen.widgetview.util.WidgetSwipeManager.MotionType.RIGHT_BOTTOM_ANGLE_MOTION;
import static com.jenshen.widgetview.util.WidgetSwipeManager.MotionType.RIGHT_TOP_ANGLE_MOTION;
import static com.jenshen.widgetview.util.WidgetSwipeManager.MotionType.WIDGET_MOTION;

public class WidgetSwipeManager implements View.OnTouchListener {

    private final int pointWidth;
    private final int pointHeight;
    private final WidgetPosition widgetPosition;
    private boolean isDragMotion;
    private float lastXPosition;
    private float lastYPosition;
    private WidgetMotionInfo motionInfo;
    @Nullable
    private OnWidgetMoveUpListener onWidgetMoveUpListener;

    public WidgetSwipeManager(int pointWidth, int pointHeight, WidgetPosition widgetPosition) {
        this.pointWidth = pointWidth;
        this.pointHeight = pointHeight;
        this.widgetPosition = widgetPosition;
    }

    public void setOnWidgetMoveUpListener(@Nullable OnWidgetMoveUpListener onWidgetMoveUpListener) {
        this.onWidgetMoveUpListener = onWidgetMoveUpListener;
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
       /* if (isDragMotion) {
            return true;
        }*/
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        final float shiftX = x - lastXPosition;
        final float shiftY = y - lastYPosition;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return actionDown(view, layoutParams, motionEvent);
            case MotionEvent.ACTION_MOVE:
                if (motionInfo == null) {
                    return false;
                }
                switch (motionInfo.getMotionType()) {
                    case LEFT_TOP_ANGLE_MOTION:
                        view.setX(motionInfo.getLastWidgetPositionX() + shiftX);
                        view.setY(motionInfo.getLastWidgetPositionY() + shiftY);
                        layoutParams.width = Math.round(motionInfo.getLastWidth() - shiftX);
                        layoutParams.height = Math.round(motionInfo.getLastHeight() - shiftY);
                        view.setLayoutParams(layoutParams);
                        actionMove(view, motionEvent);
                        return true;
                    case RIGHT_TOP_ANGLE_MOTION:
                        view.setY(motionInfo.getLastWidgetPositionY() + shiftY);
                        layoutParams.width = Math.round(motionInfo.getLastWidth() + shiftX);
                        layoutParams.height = Math.round(motionInfo.getLastHeight() - shiftY);
                        view.setLayoutParams(layoutParams);
                        actionMove(view, motionEvent);
                        return true;
                    case LEFT_BOTTOM_ANGLE_MOTION:
                        view.setX(motionInfo.getLastWidgetPositionX() + shiftX);
                        layoutParams.width = Math.round(motionInfo.getLastWidth() - shiftX);
                        layoutParams.height = Math.round(motionInfo.getLastHeight() + shiftY);
                        view.setLayoutParams(layoutParams);
                        actionMove(view, motionEvent);
                        return true;
                    case RIGHT_BOTTOM_ANGLE_MOTION:
                        layoutParams.width = Math.round(motionInfo.getLastWidth() + shiftX);
                        layoutParams.height = Math.round(motionInfo.getLastHeight() + shiftY);
                        view.setLayoutParams(layoutParams);
                        actionMove(view, motionEvent);
                        return true;
                    case WIDGET_MOTION:
                        return true;
                    default:
                        return false;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                actionUp(view, layoutParams);
                return true;
            default:
                return false;
        }
    }

    private boolean actionDown(View view, ViewGroup.LayoutParams layoutParams, MotionEvent motionEvent) {
        this.lastXPosition = motionEvent.getRawX();
        this.lastYPosition = motionEvent.getRawY();
        float widgetX = view.getX();
        float widgetY = view.getY();
        float fingerX = motionEvent.getX() + widgetX;
        float fingerY = motionEvent.getY() + widgetY;
        if (widgetX <= fingerX && widgetX + pointWidth >= fingerX &&
                widgetY <= fingerY && widgetY + pointHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, layoutParams.width, layoutParams.height, LEFT_TOP_ANGLE_MOTION);
            return true;
        } else if (widgetX + view.getMeasuredWidth() - pointWidth <= fingerX && widgetX + view.getMeasuredWidth() >= fingerX &&
                widgetY <= fingerY && widgetY + pointHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, layoutParams.width, layoutParams.height, RIGHT_TOP_ANGLE_MOTION);
            return true;
        } else if (widgetX <= fingerX && widgetX + pointWidth >= fingerX &&
                widgetY + view.getMeasuredHeight() - pointHeight <= fingerY && widgetY + view.getMeasuredHeight() >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, layoutParams.width, layoutParams.height, LEFT_BOTTOM_ANGLE_MOTION);
            return true;
        } else if (widgetX + view.getMeasuredWidth() - pointWidth <= fingerX && widgetX + view.getMeasuredWidth() >= fingerX &&
                widgetY + view.getMeasuredHeight() - pointHeight <= fingerY && widgetY + view.getMeasuredHeight() >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, layoutParams.width, layoutParams.height, RIGHT_BOTTOM_ANGLE_MOTION);
            return true;
        } else {
            return false;
        }
    }

    private void actionMove(View view, MotionEvent motionEvent) {
    }

    private void actionUp(View view, ViewGroup.LayoutParams layoutParams) {
        if (onWidgetMoveUpListener != null) {
            motionInfo.setCurrentWidgetPositionX(view.getX());
            motionInfo.setCurrentWidgetPositionY(view.getY());
            motionInfo.setCurrentHeight(layoutParams.height);
            motionInfo.setCurrentWidth(layoutParams.width);
            onWidgetMoveUpListener.onMoveUp(view, widgetPosition, motionInfo);
        }
    }

    @IntDef({LEFT_TOP_ANGLE_MOTION, RIGHT_TOP_ANGLE_MOTION, LEFT_BOTTOM_ANGLE_MOTION, RIGHT_BOTTOM_ANGLE_MOTION, WIDGET_MOTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MotionType {
        int LEFT_TOP_ANGLE_MOTION = 0;
        int RIGHT_TOP_ANGLE_MOTION = 1;
        int LEFT_BOTTOM_ANGLE_MOTION = 2;
        int RIGHT_BOTTOM_ANGLE_MOTION = 3;
        int WIDGET_MOTION = 4;
    }
}
