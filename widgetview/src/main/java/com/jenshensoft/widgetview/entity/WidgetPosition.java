package com.jenshensoft.widgetview.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class WidgetPosition implements Parcelable {

    public static final int EMPTY = -1;
    //top left
    private int topLeftColumnLine = EMPTY;
    private int topLeftRowLine = EMPTY;
    //top right
    private int topRightColumnLine = EMPTY;
    private int topRightRowLine = EMPTY;
    //bottom left
    private int bottomLeftColumnLine = EMPTY;
    private int bottomLeftRowLine = EMPTY;
    //bottom right
    private int bottomRightColumnLine = EMPTY;
    private int bottomRightRowLine = EMPTY;

    public WidgetPosition() {
    }

    public WidgetPosition(int topLeftColumnLine, int topLeftRowLine,
                          int topRightColumnLine, int topRightRowLine,
                          int bottomLeftColumnLine, int bottomLeftRowLine,
                          int bottomRightColumnLine, int bottomRightRowLine) {
        this.topLeftColumnLine = topLeftColumnLine;
        this.topLeftRowLine = topLeftRowLine;
        this.topRightColumnLine = topRightColumnLine;
        this.topRightRowLine = topRightRowLine;
        this.bottomLeftColumnLine = bottomLeftColumnLine;
        this.bottomLeftRowLine = bottomLeftRowLine;
        this.bottomRightColumnLine = bottomRightColumnLine;
        this.bottomRightRowLine = bottomRightRowLine;
    }

    public int getTopLeftColumnLine() {
        return topLeftColumnLine;
    }

    public void setTopLeftColumnLine(int topLeftColumnLine) {
        this.topLeftColumnLine = topLeftColumnLine;
    }

    public int getTopLeftRowLine() {
        return topLeftRowLine;
    }

    public void setTopLeftRowLine(int topLeftRowLine) {
        this.topLeftRowLine = topLeftRowLine;
    }

    public int getTopRightColumnLine() {
        return topRightColumnLine;
    }

    public void setTopRightColumnLine(int topRightColumnLine) {
        this.topRightColumnLine = topRightColumnLine;
    }

    public int getTopRightRowLine() {
        return topRightRowLine;
    }

    public void setTopRightRowLine(int topRightRowLine) {
        this.topRightRowLine = topRightRowLine;
    }

    public int getBottomLeftColumnLine() {
        return bottomLeftColumnLine;
    }

    public void setBottomLeftColumnLine(int bottomLeftColumnLine) {
        this.bottomLeftColumnLine = bottomLeftColumnLine;
    }

    public int getBottomLeftRowLine() {
        return bottomLeftRowLine;
    }

    public void setBottomLeftRowLine(int bottomLeftRowLine) {
        this.bottomLeftRowLine = bottomLeftRowLine;
    }

    public int getBottomRightColumnLine() {
        return bottomRightColumnLine;
    }

    public void setBottomRightColumnLine(int bottomRightColumnLine) {
        this.bottomRightColumnLine = bottomRightColumnLine;
    }

    public int getBottomRightRowLine() {
        return bottomRightRowLine;
    }

    public void setBottomRightRowLine(int bottomRightRowLine) {
        this.bottomRightRowLine = bottomRightRowLine;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.topLeftColumnLine);
        dest.writeInt(this.topLeftRowLine);
        dest.writeInt(this.topRightColumnLine);
        dest.writeInt(this.topRightRowLine);
        dest.writeInt(this.bottomLeftColumnLine);
        dest.writeInt(this.bottomLeftRowLine);
        dest.writeInt(this.bottomRightColumnLine);
        dest.writeInt(this.bottomRightRowLine);
    }

    protected WidgetPosition(Parcel in) {
        this.topLeftColumnLine = in.readInt();
        this.topLeftRowLine = in.readInt();
        this.topRightColumnLine = in.readInt();
        this.topRightRowLine = in.readInt();
        this.bottomLeftColumnLine = in.readInt();
        this.bottomLeftRowLine = in.readInt();
        this.bottomRightColumnLine = in.readInt();
        this.bottomRightRowLine = in.readInt();
    }

    public static final Parcelable.Creator<WidgetPosition> CREATOR = new Parcelable.Creator<WidgetPosition>() {
        @Override
        public WidgetPosition createFromParcel(Parcel source) {
            return new WidgetPosition(source);
        }

        @Override
        public WidgetPosition[] newArray(int size) {
            return new WidgetPosition[size];
        }
    };
}
