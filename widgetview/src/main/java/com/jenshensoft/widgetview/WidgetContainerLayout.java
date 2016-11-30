package com.jenshensoft.widgetview;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jenshen.awesomeanimation.AwesomeAnimation;
import com.jenshensoft.widgetview.entity.Point;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;
import com.jenshensoft.widgetview.entity.WidgetPosition;
import com.jenshensoft.widgetview.listener.OnWidgetMotionListener;
import com.jenshensoft.widgetview.util.BitmapUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class WidgetContainerLayout extends FrameLayout implements OnWidgetMotionListener {

    private int columnCount = 4;
    private int rowCount = 4;
    private int autoConnectAvailabilityZone = 1000;
    private boolean connectOnlyEmptyPoints = false;
    private boolean enableTrash = true;
    private int trashIcon = R.drawable.ic_delete;
    private int trashAvailabilityZone = 100;
    private List<WidgetView> widgets;
    private List<Point> points;
    private Paint paintLines;
    private Paint paintPoints;
    private boolean isOnAnimateWidget;

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

    @SuppressWarnings("unused")
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
        if (isOnAnimateWidget) {
            return;
        }
        createPoints(getMeasuredWidth(), getMeasuredHeight());
        for (WidgetView widget : widgets) {
            if (!widget.isInTouchMode()) {
                updateViewPosition(widget);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Log.e("TAG", "draw");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEnabled()) {
            drawLines(canvas);
            drawPoints(canvas);
            if (enableTrash) {
                drawTrash(canvas);
            }
        }
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof WidgetView) {
            WidgetView widgetView = (WidgetView) child;
            widgetView.setOnWidgetMoveUpListener(this);
            widgets.add(widgetView);
        } else {
            ((ViewGroup) child.getParent()).removeView(child);
            addWidget(child);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (WidgetView widget : widgets) {
            widget.setEnabled(enabled);
        }
    }

    @Override
    public void onActionDown(WidgetView view, WidgetMotionInfo motionInfo) {
        inDeleteZone = false;
    }

    boolean inDeleteZone;

    @Override
    public void onActionMove(WidgetView view, WidgetMotionInfo motionInfo) {
        float deleteX = 0;
        float deleteY = 0;
        float widgetPositionX = motionInfo.getCurrentWidgetPositionX();
        float widgetPositionY = motionInfo.getCurrentWidgetPositionY();
        if (deleteX - trashAvailabilityZone <= widgetPositionX && deleteX + trashAvailabilityZone >= widgetPositionX &&
                deleteY - trashAvailabilityZone <= widgetPositionY && deleteY + trashAvailabilityZone >= widgetPositionY) {

            inDeleteZone = true;
        } else {

            inDeleteZone = false;
        }
        invalidate();
        Log.e("TAG", "onActionMove");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActionUp(WidgetView view, WidgetMotionInfo motionInfo) {
        inDeleteZone = false;
        Point leftTopCorner = getPointByCoordinates(motionInfo.getCurrentWidgetPositionX(), motionInfo.getCurrentWidgetPositionY());
        Point rightTopCorner = getPointByCoordinates(motionInfo.getCurrentWidgetPositionX() + motionInfo.getCurrentWidth(),
                motionInfo.getCurrentWidgetPositionY());
        Point leftBottomCorner = getPointByCoordinates(motionInfo.getCurrentWidgetPositionX(),
                motionInfo.getCurrentWidgetPositionY() + motionInfo.getCurrentHeight());
        Point rightBottomCorner = getPointByCoordinates(motionInfo.getCurrentWidgetPositionX() + motionInfo.getCurrentWidth(),
                motionInfo.getCurrentWidgetPositionY() + motionInfo.getCurrentHeight());
        if (!validateNewPosition(leftTopCorner, rightTopCorner, leftBottomCorner, rightBottomCorner)) {
            setLastPosition(view, motionInfo);
        } else {
            if (leftTopCorner.getX() == leftBottomCorner.getX() &&
                    rightTopCorner.getX() == rightBottomCorner.getX() &&
                    leftTopCorner.getY() == rightTopCorner.getY() &&
                    leftBottomCorner.getY() == rightBottomCorner.getY()) {

                WidgetPosition widgetPosition = view.getWidgetPosition();
                widgetPosition.setTopLeftColumnLine(leftTopCorner.getColumn());
                widgetPosition.setTopLeftRowLine(leftTopCorner.getRow());
                widgetPosition.setTopRightColumnLine(rightTopCorner.getColumn());
                widgetPosition.setTopRightRowLine(rightTopCorner.getRow());
                widgetPosition.setBottomLeftColumnLine(leftBottomCorner.getColumn());
                widgetPosition.setBottomLeftRowLine(leftBottomCorner.getRow());
                widgetPosition.setBottomRightColumnLine(rightBottomCorner.getColumn());
                widgetPosition.setBottomRightRowLine(rightBottomCorner.getRow());

                int width = rightTopCorner.getX() - leftTopCorner.getX();
                int height = leftBottomCorner.getY() - leftTopCorner.getY();
                setNewPosition(view, motionInfo, leftTopCorner.getX(), leftTopCorner.getY(), width, height);
            } else {
                throw new RuntimeException("Something went wrong");
            }
        }
    }

    public void addWidget(View view) {
        WidgetView widgetView = createWidgetView(view);
        this.addView(widgetView);
    }


    /* private methods */

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
                columnCount = attributes.getInt(R.styleable.WidgetContainerLayout_Params_widgetContainer_columnCount, columnCount);
                rowCount = attributes.getInt(R.styleable.WidgetContainerLayout_Params_widgetContainer_rowCount, rowCount);
                connectOnlyEmptyPoints = attributes.getBoolean(R.styleable.WidgetContainerLayout_Params_widgetContainer_connectOnlyEmptyPoints, connectOnlyEmptyPoints);
                enableTrash = attributes.getBoolean(R.styleable.WidgetContainerLayout_Params_widgetContainer_enableTrash, enableTrash);
                trashIcon = attributes.getResourceId(R.styleable.WidgetContainerLayout_Params_widgetContainer_trashIcon, trashIcon);
                autoConnectAvailabilityZone = attributes.getDimensionPixelOffset(R.styleable.WidgetContainerLayout_Params_widgetContainer_autoConnect_AvailabilityZone, autoConnectAvailabilityZone);
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

    private void drawTrash(Canvas canvas) {
        Bitmap deleteBitmap;
        if (inDeleteZone) {
            Log.e("TAG", "IN Zone");
            deleteBitmap = Bitmap.createScaledBitmap(BitmapUtil.getBitmap(getContext(), trashIcon), 200, 200, false);
        } else {
            Log.e("TAG", "OUT Zone");
            deleteBitmap = Bitmap.createScaledBitmap(BitmapUtil.getBitmap(getContext(), trashIcon), 100, 100, false);
        }
        canvas.drawBitmap(deleteBitmap, 0, 0, paintPoints);
    }

    private WidgetView createWidgetView(View view) {
        WidgetView widgetView = new WidgetView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        widgetView.setLayoutParams(layoutParams);
        widgetView.addView(view);
        return widgetView;
    }

    @Nullable
    private Point getPointByCoordinates(final float x, final float y) {
        Set<Point> pointsSet = new TreeSet<>(new Comparator<Point>() {
            @Override
            public int compare(Point point1, Point point2) {
                float offsets1 = Math.abs(point1.getX() - x) + Math.abs(point1.getY() - y);
                float offsets2 = Math.abs(point2.getX() - x) + Math.abs(point2.getY() - y);
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
            if (point.getX() - autoConnectAvailabilityZone <= x && point.getX() + autoConnectAvailabilityZone >= x &&
                    point.getY() - autoConnectAvailabilityZone <= y && point.getY() + autoConnectAvailabilityZone >= y) {
                pointsSet.add(point);
            }
        }
        return pointsSet.isEmpty() ? null : pointsSet.iterator().next();
    }

    private Point getPointByColumnAndRow(final int column, final int row) {
        for (Point point : points) {
            if (point.getColumn() == column && point.getRow() == row) {
                return point;
            }
        }
        throw new RuntimeException("Can't find the point for corner. Are you sure that column " + column + " and row " + row + " fall within the screen");
    }

    private void updateViewPosition(WidgetView view) {
        if (hasAnyIndexes(view)) {
            checkPointsSet(view);
            checkOppositeLines(view);
            checkSideLength(view);
        } else {
            return;
        }
        WidgetPosition widgetPosition = view.getWidgetPosition();

        Point topLeftPoint = getPointByColumnAndRow(widgetPosition.getTopLeftColumnLine(), widgetPosition.getTopLeftRowLine());
        Point topRightPoint = getPointByColumnAndRow(widgetPosition.getTopRightColumnLine(), widgetPosition.getTopRightRowLine());
        Point bottomLeftPoint = getPointByColumnAndRow(widgetPosition.getBottomLeftColumnLine(), widgetPosition.getBottomLeftRowLine());
        Point bottomRightPoint = getPointByColumnAndRow(widgetPosition.getBottomRightColumnLine(), widgetPosition.getBottomRightRowLine());

        topLeftPoint.setConnected(true);
        topRightPoint.setConnected(true);
        bottomLeftPoint.setConnected(true);
        bottomRightPoint.setConnected(true);

        int width = topRightPoint.getX() - topLeftPoint.getX();
        int height = bottomLeftPoint.getY() - topLeftPoint.getY();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        view.setX(topLeftPoint.getX());
        view.setY(topLeftPoint.getY());
    }

    private void setLastPosition(View view, WidgetMotionInfo motionInfo) {
        isOnAnimateWidget = true;
        AwesomeAnimation animation = new AwesomeAnimation.Builder(view)
                .setX(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionX(), motionInfo.getLastWidgetPositionX())
                .setY(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionY(), motionInfo.getLastWidgetPositionY())
                .setSizeX(AwesomeAnimation.SizeMode.SIZE, motionInfo.getCurrentWidth(), motionInfo.getLastWidth())
                .setSizeY(AwesomeAnimation.SizeMode.SIZE, motionInfo.getCurrentHeight(), motionInfo.getLastHeight())
                .setDuration(500)
                .build();
        animation.getAnimatorSet().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOnAnimateWidget = false;
            }
        });
        animation.start();
    }

    private void setNewPosition(View view, WidgetMotionInfo motionInfo,
                                int x, int y,
                                int width, int height) {
        isOnAnimateWidget = true;
        AwesomeAnimation animation = new AwesomeAnimation.Builder(view)
                .setX(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionX(), x)
                .setY(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionY(), y)
                .setSizeX(AwesomeAnimation.SizeMode.SIZE, motionInfo.getCurrentWidth(), width)
                .setSizeY(AwesomeAnimation.SizeMode.SIZE, motionInfo.getCurrentHeight(), height)
                .setDuration(500)
                .build();
        animation.getAnimatorSet().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isOnAnimateWidget = false;
            }
        });
        animation.start();
    }

    private boolean hasAnyIndexes(WidgetView view) {
        WidgetPosition widgetPosition = view.getWidgetPosition();
        return widgetPosition.getTopLeftColumnLine() != WidgetPosition.EMPTY ||
                widgetPosition.getTopLeftRowLine() != WidgetPosition.EMPTY ||
                widgetPosition.getTopRightColumnLine() != WidgetPosition.EMPTY ||
                widgetPosition.getTopRightRowLine() != WidgetPosition.EMPTY ||
                widgetPosition.getBottomLeftColumnLine() != WidgetPosition.EMPTY ||
                widgetPosition.getBottomLeftRowLine() != WidgetPosition.EMPTY ||
                widgetPosition.getBottomRightColumnLine() != WidgetPosition.EMPTY ||
                widgetPosition.getBottomRightRowLine() != WidgetPosition.EMPTY;
    }

    private boolean arePointsSet(WidgetView view) {
        WidgetPosition widgetPosition = view.getWidgetPosition();
        return widgetPosition.getTopLeftColumnLine() != WidgetPosition.EMPTY &&
                widgetPosition.getTopLeftRowLine() != WidgetPosition.EMPTY &&
                widgetPosition.getTopRightColumnLine() != WidgetPosition.EMPTY &&
                widgetPosition.getTopRightRowLine() != WidgetPosition.EMPTY &&
                widgetPosition.getBottomLeftColumnLine() != WidgetPosition.EMPTY &&
                widgetPosition.getBottomLeftRowLine() != WidgetPosition.EMPTY &&
                widgetPosition.getBottomRightColumnLine() != WidgetPosition.EMPTY &&
                widgetPosition.getBottomRightRowLine() != WidgetPosition.EMPTY;
    }

    private void checkPointsSet(WidgetView view) {
        if (!arePointsSet(view)) {
            WidgetPosition widgetPosition = view.getWidgetPosition();
            String errorMessage = "All pointers should be set. There are ";
            if (widgetPosition.getTopLeftColumnLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopLeft column not set";
            }
            if (widgetPosition.getTopLeftRowLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopLeft row not set";
            }
            if (widgetPosition.getTopRightColumnLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopRight column not set";
            }
            if (widgetPosition.getTopRightRowLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopRight column not set";
            }
            if (widgetPosition.getBottomLeftColumnLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopLeft column not set";
            }
            if (widgetPosition.getBottomLeftRowLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopLeft row not set";
            }
            if (widgetPosition.getBottomRightColumnLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopRight column not set";
            }
            if (widgetPosition.getBottomRightRowLine() == WidgetPosition.EMPTY) {
                errorMessage += ", TopRight column not set";
            }
            errorMessage += ";";
            throw new RuntimeException(errorMessage);
        }
    }

    private void checkOppositeLines(WidgetView view) {
        WidgetPosition widgetPosition = view.getWidgetPosition();
        if (widgetPosition.getTopLeftColumnLine() != widgetPosition.getBottomLeftColumnLine() ||
                widgetPosition.getTopRightColumnLine() != widgetPosition.getBottomRightColumnLine() ||
                widgetPosition.getTopLeftRowLine() != widgetPosition.getTopRightRowLine() ||
                widgetPosition.getBottomLeftRowLine() != widgetPosition.getBottomRightRowLine()) {
            throw new RuntimeException("Something went wrong, the widget should be a square, there are same rules for index lines " +
                    "(TopLeft column line == BottomLeft column line, " +
                    "(TopRight column line == BottomRight column line, " +
                    "(TopLeft row line == TopRight row line, " +
                    "(BottomLeft row line == BottomRight row line, " +
                    ")");
        }
    }

    private void checkSideLength(WidgetView view) {
        WidgetPosition widgetPosition = view.getWidgetPosition();
        if (widgetPosition.getTopLeftColumnLine() == widgetPosition.getTopRightColumnLine() ||
                widgetPosition.getBottomLeftColumnLine() == widgetPosition.getBottomRightColumnLine() ||
                widgetPosition.getTopLeftRowLine() == widgetPosition.getBottomLeftRowLine() ||
                widgetPosition.getTopRightRowLine() == widgetPosition.getBottomRightRowLine()) {
            throw new RuntimeException("Something went wrong, the widget sides can't be 0, there are same rules for index lines " +
                    "(TopLeft column line == TopRight column line, " +
                    "(BottomLeft column line == BottomRight column line, " +
                    "(TopLeft row line == BottomLeft row line, " +
                    "(TopRight row line == BottomRight row line)");
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean validateNewPosition(Point leftTopCorner, Point rightTopCorner, Point leftBottomCorner, Point rightBottomCorner) {
        if (leftTopCorner == null || rightTopCorner == null || leftBottomCorner == null || rightBottomCorner == null) {
            return false;
        }
        if (leftTopCorner.getColumn() == rightTopCorner.getColumn() ||
                leftBottomCorner.getColumn() == rightBottomCorner.getColumn() ||
                leftTopCorner.getRow() == leftBottomCorner.getRow() ||
                rightTopCorner.getRow() == rightBottomCorner.getRow()) {
            return false;
        }
        if (connectOnlyEmptyPoints && (leftTopCorner.isConnected() || rightTopCorner.isConnected() ||
                leftBottomCorner.isConnected() || rightBottomCorner.isConnected())) {
            return false;
        }
        return true;
    }
}
