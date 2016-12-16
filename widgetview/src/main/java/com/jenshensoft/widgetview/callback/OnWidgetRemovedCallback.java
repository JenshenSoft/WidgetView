package com.jenshensoft.widgetview.callback;


import com.jenshensoft.widgetview.WidgetView;
import com.jenshensoft.widgetview.entity.WidgetMotionInfo;

public interface OnWidgetRemovedCallback {

    void onWidgetRemoved(WidgetView view, WidgetMotionInfo motionInfo);

}
