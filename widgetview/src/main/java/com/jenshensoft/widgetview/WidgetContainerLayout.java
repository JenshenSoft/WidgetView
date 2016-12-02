package com.jenshensoft.widgetview;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jenshen.awesomeanimation.AwesomeAnimation;
import com.jenshensoft.widgetview.entity.Point;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;
import com.jenshensoft.widgetview.entity.WidgetPosition;
import com.jenshensoft.widgetview.listener.OnWidgetMotionListener;
import com.jenshensoft.widgetview.util.BitmapUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.jenshensoft.widgetview.WidgetContainerLayout.DeletePanelGravity.BOTTOM;
import static com.jenshensoft.widgetview.WidgetContainerLayout.DeletePanelGravity.LEFT;
import static com.jenshensoft.widgetview.WidgetContainerLayout.DeletePanelGravity.RIGHT;
import static com.jenshensoft.widgetview.WidgetContainerLayout.DeletePanelGravity.TOP;

public class WidgetContainerLayout extends FrameLayout implements OnWidgetMotionListener {

    private int columnCount = 4;
    private int rowCount = 4;
    private int autoConnectAvailabilityZone = -1;
    private boolean connectOnlyEmptyPoints = false;
    private boolean enableTrash = true;
    @DeletePanelGravity
    private int deletePanelGravity = TOP;
    private int deletePanelLength = -1;
    private int trashIcon = R.drawable.ic_delete;
    private int trashAvailabilityZone = -1;
    private ImageView deleteView;
    private List<WidgetView> widgets;
    private List<Point> points;
    private Paint paintLines;
    private Paint paintPoints;
    private boolean isOnAnimateWidget;
    private boolean inDeleteArea;

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
        updateDeletePanel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEnabled()) {
            drawLines(canvas);
            drawPoints(canvas);
        }
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof WidgetView) {
            WidgetView widgetView = (WidgetView) child;
            widgetView.setOnWidgetMoveUpListener(this);
            widgets.add(widgetView);
        } else if (deleteView != child) {
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
        //ignored
    }

    @Override
    public void onActionMove(WidgetView view, WidgetMotionInfo motionInfo) {
        float deleteX = deleteView.getX();
        float deleteY = deleteView.getY();
        float widgetPositionX = motionInfo.getCurrentWidgetPositionX();
        float widgetPositionY = motionInfo.getCurrentWidgetPositionY();
        if (deleteX - trashAvailabilityZone - view.getMeasuredWidth() <= widgetPositionX &&
                deleteX + deleteView.getMeasuredWidth() + trashAvailabilityZone >= widgetPositionX &&
                deleteY - trashAvailabilityZone - view.getMeasuredHeight() <= widgetPositionY &&
                deleteY + deleteView.getMeasuredHeight() + trashAvailabilityZone >= widgetPositionY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                deleteView.setImageAlpha(255);
            } else {
                deleteView.setAlpha(1f);
            }
            inDeleteArea = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                deleteView.setImageAlpha(150);
            } else {
                deleteView.setAlpha(0.7f);
            }
            inDeleteArea = false;
        }
        invalidate();
        Log.e("TAG", "onActionMove");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActionUp(final WidgetView view, WidgetMotionInfo motionInfo) {
        if (inDeleteArea) {
            AwesomeAnimation awesomeAnimation = new AwesomeAnimation.Builder(view)
                    .setX(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionX(), deleteView.getX() - view.getMeasuredWidth() / 2 + deleteView.getMeasuredWidth() / 2)
                    .setY(AwesomeAnimation.CoordinationMode.COORDINATES, motionInfo.getCurrentWidgetPositionY(), deleteView.getY() - view.getMeasuredHeight() / 2 + deleteView.getMeasuredHeight() / 2)
                    .setSizeX(AwesomeAnimation.SizeMode.SCALE, 1.0f, 0)
                    .setSizeY(AwesomeAnimation.SizeMode.SCALE, 1.0f, 0)
                    .setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setAlpha(1.0f, 0.0f)
                    .setRotation(0, 10, 0, -10, 0)
                    .build();
            awesomeAnimation.getAnimatorSet()
                    .addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeWidget(view);
                        }
                    });

            awesomeAnimation.start();

            new AwesomeAnimation.Builder(deleteView)
                    .setRotation(0, 10, 0, -10, 0)
                    .setDuration(500)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setSizeX(AwesomeAnimation.SizeMode.SCALE, 1.0f, 1.9f, 1.0f)
                    .setSizeY(AwesomeAnimation.SizeMode.SCALE, 1.0f, 1.9f, 1.0f)
                    .build()
                    .start();

        } else {
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

                    int width = Math.round(rightTopCorner.getX() - leftTopCorner.getX());
                    int height = Math.round(leftBottomCorner.getY() - leftTopCorner.getY());
                    setNewPosition(view, motionInfo, leftTopCorner.getX(), leftTopCorner.getY(), width, height);
                } else {
                    throw new RuntimeException("Something went wrong");
                }
            }
        }
    }

    @NonNull
    public List<WidgetView> getWidgets() {
        return widgets;
    }

    public void addWidget(@NonNull View view) {
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

        if (autoConnectAvailabilityZone == -1) {
            autoConnectAvailabilityZone = getContext().getResources().getDimensionPixelOffset(R.dimen.widgetView_autoConnect_availabilityZone);
        }
        if (trashAvailabilityZone == -1) {
            trashAvailabilityZone = getContext().getResources().getDimensionPixelOffset(R.dimen.widgetView_trashAvailabilityZone);
        }
        if (deletePanelLength == -1) {
            deletePanelLength = getContext().getResources().getDimensionPixelOffset(R.dimen.widgetView_deletePanel_length);
        }

        setWillNotDraw(false);
        createDeleteView();
    }

    private void createDeleteView() {
        deleteView = new ImageView(getContext());
        deleteView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        deleteView.setImageBitmap(BitmapUtil.getBitmap(getContext(), trashIcon));
        addView(deleteView, 0);
    }

    @SuppressWarnings("WrongConstant")
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetContainerLayout_Params);
            try {
                columnCount = attributes.getInt(R.styleable.WidgetContainerLayout_Params_widgetContainer_columnCount, columnCount);
                rowCount = attributes.getInt(R.styleable.WidgetContainerLayout_Params_widgetContainer_rowCount, rowCount);
                connectOnlyEmptyPoints = attributes.getBoolean(R.styleable.WidgetContainerLayout_Params_widgetContainer_connectOnlyEmptyPoints, connectOnlyEmptyPoints);
                enableTrash = attributes.getBoolean(R.styleable.WidgetContainerLayout_Params_widgetContainer_enableTrash, enableTrash);
                trashIcon = attributes.getResourceId(R.styleable.WidgetContainerLayout_Params_widgetContainer_trashIcon, trashIcon);
                deletePanelGravity = attributes.getInt(R.styleable.WidgetContainerLayout_Params_widgetContainer_deletePanelGravity, deletePanelGravity);
                autoConnectAvailabilityZone = attributes.getDimensionPixelOffset(R.styleable.WidgetContainerLayout_Params_widgetContainer_autoConnect_AvailabilityZone, autoConnectAvailabilityZone);
            } finally {
                attributes.recycle();
            }
        }
    }

    private void createPoints(int width, int height) {
        points.clear();
        float currentX = validateStartX(0);
        float currentY = validateStartY(0);
        width = (int) validateWidth(width);
        height = (int) validateHeight(height);

        float columnWidth = width / columnCount;
        float rowHeight = height / rowCount;
        for (int column = 0; column <= rowCount; column++) {
            for (int row = 0; row <= columnCount; row++) {
                points.add(new Point(currentX, currentY, column, row));
                currentY += rowHeight;
            }
            currentX += columnWidth;
            currentY = validateStartY(0);
        }
    }

    private void drawLines(Canvas canvas) {
        float currentX = validateStartX(0);
        float currentY = validateStartY(0);
        int width = (int) validateWidth(canvas.getWidth());
        int height = (int) validateHeight(canvas.getHeight());

        int columnWidth = width / columnCount;
        int rowHeight = height / rowCount;

        float startX = validateStartX(0);
        float startY = validateStartY(0);
        for (int column = 0; column <= rowCount; column++) {
            for (int row = 0; row <= columnCount; row++) {
                canvas.drawLine(startX, currentY, startX + width, currentY, paintLines);
                currentY += rowHeight;
            }
            canvas.drawLine(currentX, startY, currentX, startY + height, paintLines);
            currentX += columnWidth;
            currentY = startY;
        }
    }

    private void drawPoints(Canvas canvas) {
        for (Point point : points) {
            canvas.drawCircle(point.getX(), point.getY(), 10, paintPoints);
        }
    }

    private WidgetView createWidgetView(View view) {
        WidgetView widgetView = new WidgetView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        widgetView.setLayoutParams(layoutParams);
        widgetView.addView(view);
        return widgetView;
    }

    private float validateWidth(float width) {
        if (deletePanelGravity == LEFT || deletePanelGravity == RIGHT) {
            return width - deletePanelLength;
        } else {
            return width;
        }
    }

    private float validateHeight(float height) {
        if (deletePanelGravity == TOP || deletePanelGravity == BOTTOM) {
            return height - deletePanelLength;
        } else {
            return height;
        }
    }

    private float validateStartX(float currentX) {
        if (deletePanelGravity == LEFT) {
            return currentX + deletePanelLength;
        } else {
            return currentX;
        }
    }

    private float validateStartY(float currentY) {
        if (deletePanelGravity == TOP) {
            return currentY + deletePanelLength;
        } else {
            return currentY;
        }
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

        view.setX(topLeftPoint.getX());
        view.setY(topLeftPoint.getY());

        int width = Math.round(topRightPoint.getX() - topLeftPoint.getX());
        int height = Math.round(bottomLeftPoint.getY() - topLeftPoint.getY());
        FrameLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
    }

    private void updateDeletePanel() {
        if (enableTrash) {
            deleteView.setVisibility(VISIBLE);
            switch (deletePanelGravity) {
                case DeletePanelGravity.TOP:
                    deleteView.setX(getMeasuredWidth() / 2 - deleteView.getMeasuredWidth() / 2);
                    deleteView.setY(deletePanelLength / 2 - deleteView.getMeasuredHeight() / 2);
                    break;
                case DeletePanelGravity.BOTTOM:
                    deleteView.setX(getMeasuredWidth() / 2 - deleteView.getMeasuredWidth() / 2);
                    deleteView.setY(getMeasuredHeight() - (deletePanelLength / 2) - deleteView.getMeasuredHeight() / 2);
                    break;
                case DeletePanelGravity.LEFT:
                    deleteView.setX(deletePanelLength / 2 - deleteView.getMeasuredWidth() / 2);
                    deleteView.setY(getMeasuredHeight() / 2 - deleteView.getMeasuredHeight() / 2);
                    break;
                case DeletePanelGravity.RIGHT:
                    deleteView.setX(getMeasuredWidth() - (deletePanelLength / 2) - deleteView.getMeasuredWidth() / 2);
                    deleteView.setY(getMeasuredHeight() / 2 - deleteView.getMeasuredHeight() / 2);
                    break;
            }
        } else {
            deleteView.setVisibility(GONE);
        }
    }

    private void removeWidget(WidgetView view) {
        view.setOnWidgetMoveUpListener(null);
        ViewParent parent = view.getParent();
        ((ViewGroup) parent).removeView(view);
        widgets.remove(view);
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
                                float x, float y,
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

    @IntDef({TOP, BOTTOM, LEFT, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DeletePanelGravity {
        int TOP = 0;
        int BOTTOM = 1;
        int LEFT = 2;
        int RIGHT = 3;
    }
}
