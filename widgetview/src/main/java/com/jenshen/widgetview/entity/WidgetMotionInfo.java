package com.jenshen.widgetview.entity;


import com.jenshen.widgetview.util.WidgetSwipeManager;

public class WidgetMotionInfo {

    private final float lastWidgetPositionX;
    private final float lastWidgetPositionY;
    private final int lastWidth;
    private final int lastHeight;

    @WidgetSwipeManager.MotionType
    private int motionType;

    private float currentWidgetPositionX;
    private float currentWidgetPositionY;
    private int currentWidth;
    private int currentHeight;

    public WidgetMotionInfo(float lastWidgetPositionX, float lastWidgetPositionY, int lastWidth, int lastHeight, @WidgetSwipeManager.MotionType int motionType) {
        this.lastWidgetPositionX = lastWidgetPositionX;
        this.lastWidgetPositionY = lastWidgetPositionY;
        this.lastWidth = lastWidth;
        this.lastHeight = lastHeight;
        this.motionType = motionType;
    }

    public float getCurrentWidgetPositionX() {
        return currentWidgetPositionX;
    }

    public void setCurrentWidgetPositionX(float currentWidgetPositionX) {
        this.currentWidgetPositionX = currentWidgetPositionX;
    }

    public float getCurrentWidgetPositionY() {
        return currentWidgetPositionY;
    }

    public void setCurrentWidgetPositionY(float currentWidgetPositionY) {
        this.currentWidgetPositionY = currentWidgetPositionY;
    }

    public int getCurrentWidth() {
        return currentWidth;
    }

    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    public float getLastWidgetPositionY() {
        return lastWidgetPositionY;
    }

    public float getLastWidgetPositionX() {
        return lastWidgetPositionX;
    }

    public int getLastWidth() {
        return lastWidth;
    }

    public int getLastHeight() {
        return lastHeight;
    }

    @WidgetSwipeManager.MotionType
    public int getMotionType() {
        return motionType;
    }

    public void setMotionType(int motionType) {
        this.motionType = motionType;
    }
}
