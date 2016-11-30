package com.jenshensoft.widgetview.util;


import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.jenshensoft.widgetview.WidgetView;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;
import com.jenshensoft.widgetview.listener.OnWidgetMotionListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.jenshensoft.widgetview.util.WidgetSwipeManager.MotionType.LEFT_BOTTOM_ANGLE_MOTION;
import static com.jenshensoft.widgetview.util.WidgetSwipeManager.MotionType.LEFT_TOP_ANGLE_MOTION;
import static com.jenshensoft.widgetview.util.WidgetSwipeManager.MotionType.RIGHT_BOTTOM_ANGLE_MOTION;
import static com.jenshensoft.widgetview.util.WidgetSwipeManager.MotionType.RIGHT_TOP_ANGLE_MOTION;
import static com.jenshensoft.widgetview.util.WidgetSwipeManager.MotionType.WIDGET_MOTION;

public class WidgetSwipeManager implements View.OnTouchListener {

    private final int pointWidth;
    private final int pointHeight;
    private final boolean dragAndDropByLongClick;
    private final GestureDetector gestureDetector;
    private float lastXPosition;
    private float lastYPosition;

    private int motionAction = -1;

    @Nullable
    private WidgetMotionInfo motionInfo;
    @Nullable
    private OnWidgetMotionListener onWidgetMotionListener;
    @Nullable
    private View view;

    public WidgetSwipeManager(Context context, int pointWidth, int pointHeight, boolean dragAndDropByLongClick) {
        this.pointWidth = pointWidth;
        this.pointHeight = pointHeight;
        this.dragAndDropByLongClick = dragAndDropByLongClick;
        this.gestureDetector = new GestureDetector(context, new LongPressGestureDetector());
    }

    public void setOnWidgetMotionListener(@Nullable OnWidgetMotionListener onWidgetMotionListener) {
        this.onWidgetMotionListener = onWidgetMotionListener;
    }

    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        final float shiftX = x - lastXPosition;
        final float shiftY = y - lastYPosition;

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return actionDown(view, motionEvent);
            case MotionEvent.ACTION_MOVE:
                if (motionInfo == null) {
                    return true;
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
                        view.setX(motionInfo.getLastWidgetPositionX() + shiftX);
                        view.setY(motionInfo.getLastWidgetPositionY() + shiftY);
                        actionMove(view, motionEvent);
                        return true;
                    default:
                        return true;
                }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                actionUp(view);
                return true;
            default:
                return false;
        }
    }

    public boolean isInTouchMode() {
        return motionAction == MotionEvent.ACTION_DOWN || motionAction == MotionEvent.ACTION_MOVE;
    }

    private boolean actionDown(View view, MotionEvent motionEvent) {
        motionAction =  MotionEvent.ACTION_DOWN;
        this.view = view;
        if (dragAndDropByLongClick) {
            gestureDetector.onTouchEvent(motionEvent);
        }
        this.lastXPosition = motionEvent.getRawX();
        this.lastYPosition = motionEvent.getRawY();
        float widgetX = view.getX();
        float widgetY = view.getY();
        float fingerX = motionEvent.getX() + widgetX;
        float fingerY = motionEvent.getY() + widgetY;
        int widgetHeight = view.getMeasuredHeight();
        int widgetWidth = view.getMeasuredWidth();
        if (widgetX <= fingerX && widgetX + pointWidth >= fingerX &&
                widgetY <= fingerY && widgetY + pointHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, widgetWidth, widgetHeight, LEFT_TOP_ANGLE_MOTION);
        } else if (widgetX + widgetWidth - pointWidth <= fingerX && widgetX + widgetWidth >= fingerX &&
                widgetY <= fingerY && widgetY + pointHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, widgetWidth, widgetHeight,  RIGHT_TOP_ANGLE_MOTION);
        } else if (widgetX <= fingerX && widgetX + pointWidth >= fingerX &&
                widgetY + widgetHeight - pointHeight <= fingerY && widgetY + widgetHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, widgetWidth, widgetHeight,  LEFT_BOTTOM_ANGLE_MOTION);
        } else if (widgetX + widgetWidth - pointWidth <= fingerX && widgetX + widgetWidth >= fingerX &&
                widgetY + widgetHeight - pointHeight <= fingerY && widgetY + widgetHeight >= fingerY) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, widgetWidth, widgetHeight,  RIGHT_BOTTOM_ANGLE_MOTION);
        } else if (!dragAndDropByLongClick) {
            motionInfo = new WidgetMotionInfo(widgetX, widgetY, widgetWidth, widgetHeight, WIDGET_MOTION);
        }

        if (onWidgetMotionListener != null && motionInfo != null) {
            motionInfo.setCurrentWidgetPositionX(view.getX());
            motionInfo.setCurrentWidgetPositionY(view.getY());
            motionInfo.setCurrentWidth(view.getMeasuredWidth());
            motionInfo.setCurrentHeight(view.getMeasuredHeight());
            onWidgetMotionListener.onActionMove((WidgetView) view, motionInfo);
        }
        return true;
    }

    private void actionMove(View view, MotionEvent motionEvent) {
        motionAction =  MotionEvent.ACTION_MOVE;
        if (onWidgetMotionListener != null && motionInfo != null) {
            motionInfo.setCurrentWidgetPositionX(view.getX());
            motionInfo.setCurrentWidgetPositionY(view.getY());
            motionInfo.setCurrentWidth(view.getMeasuredWidth());
            motionInfo.setCurrentHeight(view.getMeasuredHeight());
            onWidgetMotionListener.onActionMove((WidgetView) view, motionInfo);
        }
    }

    private void actionUp(View view) {
        if (onWidgetMotionListener != null && motionInfo != null) {
            motionInfo.setCurrentWidgetPositionX(view.getX());
            motionInfo.setCurrentWidgetPositionY(view.getY());
            motionInfo.setCurrentWidth(view.getMeasuredWidth());
            motionInfo.setCurrentHeight(view.getMeasuredHeight());
            onWidgetMotionListener.onActionUp((WidgetView) view, motionInfo);
        }
        this.motionAction =  MotionEvent.ACTION_UP;
        this.motionInfo = null;
        this.view = null;
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

    private class LongPressGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            if (motionInfo != null && motionAction == MotionEvent.ACTION_DOWN) {
                motionInfo.setMotionType(WIDGET_MOTION);
            } else if (motionAction == MotionEvent.ACTION_DOWN && view != null) {
                motionInfo = new WidgetMotionInfo(view.getX(), view.getY(), view.getMeasuredWidth(), view.getMeasuredHeight(), WIDGET_MOTION);
            }
        }
    }
}
