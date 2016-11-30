package com.jenshensoft.widgetview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.jenshensoft.widgetview.entity.WidgetPosition;
import com.jenshensoft.widgetview.listener.OnWidgetMotionListener;
import com.jenshensoft.widgetview.util.WidgetSwipeManager;

import static com.jenshensoft.widgetview.entity.WidgetPosition.EMPTY;
import static com.jenshensoft.widgetview.util.BitmapUtil.getBitmap;

public class WidgetView extends FrameLayout {

    private int pointHeight = 50;
    private int pointWidth = 50;
    private int borderOffset = 25;
    private boolean dragAndDropByLongClick = false;
    private int pointIcon = R.drawable.ic_point_angle;
    private boolean isPaddingValidated;
    private WidgetSwipeManager swipeManager;
    private Paint paintPoints;
    private WidgetPosition widgetPosition;
    private Bitmap cornerBitmap;

    public WidgetView(@NonNull Context context) {
        super(context);
        if (!isInEditMode()) {
            initAttr(null);
        }
        init();
    }

    public WidgetView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    public WidgetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WidgetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.widgetPosition = widgetPosition;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.widgetPosition = ss.widgetPosition;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isEnabled() &&!isPaddingValidated) {
            setPadding(getPaddingLeft() + borderOffset, getPaddingTop() + borderOffset, getPaddingRight() + borderOffset, getPaddingBottom() + borderOffset);
            isPaddingValidated = true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            super.dispatchTouchEvent(ev);
            return swipeManager.onTouch(this, ev);
        }
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isEnabled()) {
            drawCornersPoints(canvas);
        }
    }

    public boolean isInTouchMode() {
        return swipeManager.isInTouchMode();
    }

    public void setOnWidgetMoveUpListener(@Nullable OnWidgetMotionListener onWidgetMotionListener) {
        swipeManager.setOnWidgetMotionListener(onWidgetMotionListener);
    }

    public WidgetPosition getWidgetPosition() {
        return widgetPosition;
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetView_Params);
            try {
                pointHeight = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointHeight, pointHeight);
                pointWidth = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointWidth, pointWidth);
                borderOffset = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_borderOffset, borderOffset);
                pointIcon = attributes.getResourceId(R.styleable.WidgetView_Params_widgetView_pointIcon, pointIcon);
                dragAndDropByLongClick = attributes.getBoolean(R.styleable.WidgetView_Params_widgetView_dragAndDrop_byLongClick, dragAndDropByLongClick);

                widgetPosition = new WidgetPosition(
                        //top left
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_topLeft_columnLine, EMPTY),
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_topLeft_rowLine, EMPTY),
                        //top right
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_topRight_columnLine, EMPTY),
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_topRight_rowLine, EMPTY),
                        //bottom left
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_bottomLeft_columnLine, EMPTY),
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_bottomLeft_rowLine, EMPTY),
                        //bottom right
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_bottomRight_columnLine, EMPTY),
                        attributes.getInt(R.styleable.WidgetView_Params_widgetView_corner_bottomRight_rowLine, EMPTY));
            } finally {
                attributes.recycle();
            }
        }
    }

    private void init() {
        if (widgetPosition == null) {
            widgetPosition = new WidgetPosition();
        }
        setSaveEnabled(true);
        if (getId() == -1) {
            if (widgetPosition.getTopLeftColumnLine() != EMPTY &&
                    widgetPosition.getTopLeftRowLine() != EMPTY &&
                    widgetPosition.getTopRightColumnLine() != EMPTY &&
                    widgetPosition.getTopRightRowLine() != EMPTY &&
                    widgetPosition.getBottomLeftColumnLine() != EMPTY &&
                    widgetPosition.getBottomLeftRowLine() != EMPTY &&
                    widgetPosition.getBottomRightColumnLine() != EMPTY &&
                    widgetPosition.getBottomRightRowLine() != EMPTY) {
                String number = widgetPosition.getTopLeftColumnLine() + "" +
                        widgetPosition.getTopLeftRowLine() + "" +
                        widgetPosition.getTopRightColumnLine() + "" +
                        widgetPosition.getBottomLeftColumnLine() + "" + widgetPosition.getTopLeftColumnLine() + "" +
                        widgetPosition.getBottomLeftRowLine() + "" +
                        widgetPosition.getBottomRightColumnLine() + "" +
                        widgetPosition.getBottomRightRowLine();
                setId(Integer.valueOf(number));
            }
        }
        swipeManager = new WidgetSwipeManager(getContext(), pointWidth, pointHeight, dragAndDropByLongClick);
        paintPoints = new Paint();
        paintPoints.setStyle(Paint.Style.FILL);
        paintPoints.setColor(Color.GREEN);
        paintPoints.setStrokeWidth(5);
        cornerBitmap = Bitmap.createScaledBitmap(getBitmap(getContext(), pointIcon), pointWidth, pointHeight, false);
    }

    private void drawCornersPoints(Canvas canvas) {
        canvas.drawBitmap(cornerBitmap, 0, 0, paintPoints);
        canvas.drawBitmap(cornerBitmap, getMeasuredWidth() - pointWidth, 0, paintPoints);
        canvas.drawBitmap(cornerBitmap, 0, getMeasuredHeight() - pointHeight, paintPoints);
        canvas.drawBitmap(cornerBitmap, getMeasuredWidth() - pointWidth, getMeasuredHeight() - pointHeight, paintPoints);
    }


    /* inner types */

    private static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        WidgetPosition widgetPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.widgetPosition = in.readParcelable(WidgetPosition.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(widgetPosition, flags);
        }
    }
}
