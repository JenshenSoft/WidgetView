package com.jenshensoft.widgetview.entity;

public class Point {
    private int x;
    private int y;
    private int column;
    private int row;
    private boolean isConnected;

    public Point(int x, int y, int column, int row) {
        this.x = x;
        this.y = y;
        this.column = column;
        this.row = row;
    }

    public int getX() {
        return x;
    }

    public int getY() {
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