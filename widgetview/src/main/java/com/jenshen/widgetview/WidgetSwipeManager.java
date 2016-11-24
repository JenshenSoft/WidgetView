package com.jenshen.widgetview;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class WidgetSwipeManager implements View.OnTouchListener {

    private View rootView;
    private boolean isDragMotion;

    public WidgetSwipeManager(View rootView) {
        this.rootView = rootView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
       /* if (isDragMotion) {
            return true;
        }*/


        switch (view.getId()) {
            case WidgetView.AnglePosition.LEFT_TOP_ANGLE:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE");
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                        layoutParams.width = (int) (layoutParams.width + x);
                        layoutParams.height = (int) (layoutParams.height + y);
                        rootView.setLayoutParams(layoutParams);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP");
                        return true;
                }
                return true;
            case WidgetView.AnglePosition.RIGHT_TOP_ANGLE:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE");
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                        layoutParams.width = (int) (layoutParams.width + x);
                        layoutParams.height = (int) (layoutParams.height + y);
                        rootView.setLayoutParams(layoutParams);
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP");
                        return true;
                }
                return true;
            case WidgetView.AnglePosition.LEFT_BOTTOM_ANGLE:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE");
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                        //layoutParams.width = (int) (layoutParams.width + x / 2);
                        layoutParams.height = (int) (layoutParams.height + y / 2);
                        //rootView.setY(rootView.getY() + y - (view.getMeasuredHeight() / 2));
                        rootView.setLayoutParams(layoutParams);
                        rootView.setX(rootView.getX() + x - (view.getMeasuredWidth() / 2));
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP");
                        return true;
                }
                return true;
            case WidgetView.AnglePosition.RIGHT_BOTTOM_ANGLE:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE");
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        /*final ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
                        layoutParams.width = (int) (layoutParams.width + x - (view.getMeasuredWidth() / 2));
                        layoutParams.height = (int) (layoutParams.height + y - (view.getMeasuredHeight() / 2));
                        rootView.setLayoutParams(layoutParams);*/
                        rootView.setRight((int) (rootView.getMeasuredWidth() + x - (view.getMeasuredWidth() / 2)));
                        rootView.setBottom((int) (rootView.getMeasuredHeight() + x - (view.getMeasuredHeight() / 2)));
                        rootView.invalidate();

                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP");
                        return true;
                }
                return true;
            default:
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("TAG", "ACTION_DOWN Widget");
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Log.e("TAG", "ACTION_MOVE Widget");
                        return true;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        Log.e("TAG", "ACTION_UP Widget");
                        return true;
                }

        }
        return false;
    }
}
