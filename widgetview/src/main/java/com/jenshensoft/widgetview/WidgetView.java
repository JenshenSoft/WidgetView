package com.jenshensoft.widgetview;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.jenshensoft.widgetview.entity.WidgetPosition;
import com.jenshensoft.widgetview.listener.OnWidgetMoveUpListener;
import com.jenshensoft.widgetview.util.WidgetSwipeManager;

import static com.jenshensoft.widgetview.entity.WidgetPosition.EMPTY;

public class WidgetView extends FrameLayout {

    private int pointHeight = 50;
    private int pointWidth = 50;
    private boolean dragAndDropByLongClick = true;
    private int pointIcon = R.drawable.ic_point_angle;
    private boolean isDragMode;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WidgetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawCornersPoints(canvas);
    }

    public void setOnWidgetMoveUpListener(@Nullable OnWidgetMoveUpListener onWidgetMoveUpListener) {
        swipeManager.setOnWidgetMoveUpListener(onWidgetMoveUpListener);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetView_Params);
            try {
                pointHeight = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointHeight, pointHeight);
                pointWidth = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointWidth, pointWidth);
                pointIcon = attributes.getResourceId(R.styleable.WidgetView_Params_widgetView_pointIcon, pointIcon);
                dragAndDropByLongClick = attributes.getBoolean(R.styleable.WidgetContainerLayout_Params_widgetContainer_autoConnect_AvailabilityZone, dragAndDropByLongClick);

                widgetPosition = new WidgetPosition(
                        //top left
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_topLeft_columnLine, EMPTY),
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_topLeft_rowLine, EMPTY),
                        //top right
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_topRight_columnLine, EMPTY),
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_topRight_rowLine, EMPTY),
                        //bottom left
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_bottomLeft_columnLine, EMPTY),
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_bottomLeft_rowLine, EMPTY),
                        //bottom right
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_bottomRight_columnLine, EMPTY),
                        attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_corner_bottomRight_rowLine, EMPTY));
            } finally {
                attributes.recycle();
            }
        }
    }

    private void init() {
        if (widgetPosition == null) {
            widgetPosition = new WidgetPosition();
        }
        swipeManager = new WidgetSwipeManager(getContext(), pointWidth, pointHeight, dragAndDropByLongClick, widgetPosition);
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

    private Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
