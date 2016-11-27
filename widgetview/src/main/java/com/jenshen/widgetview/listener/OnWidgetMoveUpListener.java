package com.jenshen.widgetview.listener;


import android.view.View;

import com.jenshen.widgetview.entity.WidgetMotionInfo;
import com.jenshen.widgetview.entity.WidgetPosition;

public interface OnWidgetMoveUpListener {

    void onMoveUp(View view, WidgetPosition widgetPosition, WidgetMotionInfo motionInfo);
}
