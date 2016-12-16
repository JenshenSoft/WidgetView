package com.jenshensoft.widgetview.callback;


import com.jenshensoft.widgetview.WidgetView;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;

public interface OnWidgetMotionsCallbacks {

    void onActionDown(WidgetView view, WidgetMotionInfo motionInfo);

    void onActionMove(WidgetView view, WidgetMotionInfo motionInfo);

    void onActionUp(WidgetView view, WidgetMotionInfo motionInfo);
}
