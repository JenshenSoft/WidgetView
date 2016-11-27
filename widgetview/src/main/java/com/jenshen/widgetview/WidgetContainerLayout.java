package com.jenshen.widgetview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.jenshen.widgetview.entity.Point;
import com.jenshen.widgetview.entity.WidgetMotionInfo;
import com.jenshen.widgetview.entity.WidgetPosition;
import com.jenshen.widgetview.listener.OnWidgetMoveUpListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WidgetContainerLayout extends FrameLayout implements OnWidgetMoveUpListener {

    private int columnCount = 4;
    private int rowCount = 4;
    private int autoConnectOffset = 100;
    private List<WidgetView> widgets;
    private List<Point> points;
    private Paint paintLines;
    private Paint paintPoints;

    public WidgetContainerLayout(@NonNull Context context) {
        super(context);
    }

    public WidgetContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    public WidgetContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WidgetContainerLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            initAttr(attrs);
        }
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        createPoints(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);
        drawPoints(canvas);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof WidgetView) {
            WidgetView widgetView = (WidgetView) child;
            widgetView.setOnWidgetMoveUpListener(this);
            widgets.add(widgetView);
        } else {
            throw new RuntimeException("Child of this container mast be WidgetView not a " + child.getClass().getSimpleName());
        }
    }

    @Override
    public void onMoveUp(View view, WidgetPosition widgetPosition, WidgetMotionInfo motionInfo) {
        Point leftTopCorner = getPointForCorner(motionInfo.getCurrentWidgetPositionX(), motionInfo.getCurrentWidgetPositionY());
        if (leftTopCorner == null) {
            setLastPosition(view, motionInfo);
        }
        Point rightTopCorner = getPointForCorner(motionInfo.getCurrentWidgetPositionX() + motionInfo.getCurrentWidth(),
                motionInfo.getCurrentWidgetPositionY());
        if (rightTopCorner == null) {
            setLastPosition(view, motionInfo);
        }
        Point leftBottomCorner = getPointForCorner(motionInfo.getCurrentWidgetPositionX(),
                motionInfo.getCurrentWidgetPositionY() + motionInfo.getCurrentHeight());
        if (leftBottomCorner == null) {
            setLastPosition(view, motionInfo);
        }
        Point rightBottomCorner = getPointForCorner(motionInfo.getCurrentWidgetPositionX() + motionInfo.getCurrentWidth(),
                motionInfo.getCurrentWidgetPositionY() + motionInfo.getCurrentHeight());
        if (rightBottomCorner == null) {
            setLastPosition(view, motionInfo);
        } else {
            setNewPosition(view, widgetPosition, leftTopCorner, rightTopCorner, leftBottomCorner, rightBottomCorner);
        }
    }

    private void setLastPosition(View view, WidgetMotionInfo motionInfo) {

    }

    private void setNewPosition(View view, WidgetPosition widgetPosition,
                                Point leftTopCorner, Point rightTopCorner,
                                Point leftBottomCorner, Point rightBottomCorner) {

    }

    @Nullable
    private Point getPointForCorner(final float x, final float y) {
        Set<Point> pointsSet = new TreeSet<>(new Comparator<Point>() {
            @Override
            public int compare(Point point1, Point point2) {
                float offsets1 = Math.abs(point1.getX() - x)+ Math.abs(point1.getY() - y);
                float offsets2 = Math.abs(point2.getX() - x)+ Math.abs(point2.getY() - y);
                if (offsets1 > offsets2) {
                    return 1;
                } else if (offsets1 < offsets2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (Point point : points) {
            if (point.getX() - autoConnectOffset <= x && point.getX() + autoConnectOffset >= x &&
                    point.getY() - autoConnectOffset <= y && point.getY() + autoConnectOffset >= y) {
                pointsSet.add(point);
            }
        }
        return pointsSet.isEmpty() ? null : pointsSet.iterator().next();
    }

    private void init() {
        points = new ArrayList<>();
        widgets = new ArrayList<>();
        paintLines = new Paint();
        paintLines.setColor(Color.BLUE);
        paintLines.setStrokeWidth(5);
        paintPoints = new Paint();
        paintPoints.setColor(Color.GREEN);
        paintPoints.setStrokeWidth(5);
        setWillNotDraw(false);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetContainerLayout_Params);
            try {
                columnCount = attributes.getDimensionPixelOffset(R.styleable.WidgetContainerLayout_Params_widgetContainer_columnCount, columnCount);
                rowCount = attributes.getDimensionPixelOffset(R.styleable.WidgetContainerLayout_Params_widgetContainer_rowCount, rowCount);
            } finally {
                attributes.recycle();
            }
        }
    }

    private void createPoints(int width, int height) {
        if (!points.isEmpty()) {
            points.clear();
        }
        int columnWidth = width / columnCount;
        int rowHeight = height / rowCount;
        int currentX = 0;
        int currentY = 0;
        for (int column = 0; column <= rowCount; column++) {
            for (int row = 0; row <= columnCount; row++) {
                points.add(new Point(currentX, currentY, column, row));
                currentY += rowHeight;
            }
            currentX += columnWidth;
            currentY = 0;
        }
    }

    private void drawLines(Canvas canvas) {
        int columnWidth = canvas.getWidth() / columnCount;
        int rowHeight = canvas.getHeight() / rowCount;
        float currentX = 0;
        float currentY = 0;
        for (int column = 0; column <= rowCount; column++) {
            canvas.drawLine(currentX, 0, currentX, canvas.getHeight(), paintLines);
            currentX += columnWidth;
        }

        for (int row = 0; row <= columnCount; row++) {
            canvas.drawLine(0, currentY, canvas.getWidth(), currentY, paintLines);
            currentY += rowHeight;
        }
    }

    private void drawPoints(Canvas canvas) {
        for (Point point : points) {
            canvas.drawCircle(point.getX(), point.getY(), 10, paintPoints);
        }
    }
}
