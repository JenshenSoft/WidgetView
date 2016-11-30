package com.jenshensoft.widgetview.listener;


import com.jenshensoft.widgetview.WidgetView;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;

public interface OnWidgetMotionListener {

    void onActionDown(WidgetView view, WidgetMotionInfo motionInfo);

    void onActionMove(WidgetView view, WidgetMotionInfo motionInfo);

    void onActionUp(WidgetView view, WidgetMotionInfo motionInfo);
}
