package com.jenshensoft.widgetview.entity;

public class Point {
    private float x;
    private float y;
    private int column;
    private int row;
    private boolean isConnected;

    public Point(float x, float y, int column, int row) {
        this.x = x;
        this.y = y;
        this.column = column;
        this.row = row;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}