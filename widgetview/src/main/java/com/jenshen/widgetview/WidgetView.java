package com.jenshen.widgetview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static com.jenshen.widgetview.WidgetView.AnglePosition.LEFT_BOTTOM_ANGLE;
import static com.jenshen.widgetview.WidgetView.AnglePosition.LEFT_TOP_ANGLE;
import static com.jenshen.widgetview.WidgetView.AnglePosition.RIGHT_BOTTOM_ANGLE;
import static com.jenshen.widgetview.WidgetView.AnglePosition.RIGHT_TOP_ANGLE;


public class WidgetView extends FrameLayout {

    private List<ImageView> angles;
    private int pointHeight = 100;
    private int pointWidth = 100;
    private boolean isDragMode;
    private boolean isPaddingValidated;
    private WidgetSwipeManager swipeManager;

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

    public WidgetView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        return swipeManager.onTouch(this, ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setAnglesPosition();
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetView_Params);
            try {
                pointHeight = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointHeight, pointHeight);
                pointWidth = attributes.getDimensionPixelOffset(R.styleable.WidgetView_Params_widgetView_pointWidth, pointWidth);
            } finally {
                attributes.recycle();
            }
        }
    }

    private void init() {
        angles = new ArrayList<>();
        swipeManager = new WidgetSwipeManager(this);
        addAngles();
    }

    private void addAngles() {
        for (int i = 0; i < 4; i++) {
            final ImageView imageView = new ImageView(getContext());
            imageView.setId(i);
            imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setOnTouchListener(swipeManager);
            Bitmap bm = getBitmap(getContext(), R.drawable.ic_point_angle);
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bm, pointWidth, pointHeight, false));
            addView(imageView);
            angles.add(imageView);
        }
    }

    private void setAnglesPosition() {
        final ImageView leftTopPoint = angles.get(LEFT_TOP_ANGLE);
        leftTopPoint.setX(0);
        leftTopPoint.setY(0);

        final ImageView rightTopPoint = angles.get(RIGHT_TOP_ANGLE);
        rightTopPoint.setX(getMeasuredWidth() - rightTopPoint.getMeasuredWidth());
        rightTopPoint.setY(0);

        final ImageView leftBottomPoint = angles.get(LEFT_BOTTOM_ANGLE);
        leftBottomPoint.setX(0);
        leftBottomPoint.setY(getMeasuredHeight() - leftBottomPoint.getMeasuredHeight());

        final ImageView rightBottomPoint = angles.get(RIGHT_BOTTOM_ANGLE);
        rightBottomPoint.setX(getMeasuredWidth() - rightBottomPoint.getMeasuredWidth());
        rightBottomPoint.setY(getMeasuredHeight() - rightBottomPoint.getMeasuredHeight());
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

    @IntDef({LEFT_TOP_ANGLE, RIGHT_TOP_ANGLE, LEFT_BOTTOM_ANGLE, RIGHT_BOTTOM_ANGLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnglePosition {
        int LEFT_TOP_ANGLE = 0;
        int RIGHT_TOP_ANGLE = 1;
        int LEFT_BOTTOM_ANGLE = 2;
        int RIGHT_BOTTOM_ANGLE = 3;
    }
}
