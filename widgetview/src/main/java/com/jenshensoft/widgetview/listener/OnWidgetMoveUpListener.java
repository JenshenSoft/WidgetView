package com.jenshensoft.widgetview.listener;


import android.view.View;

import com.jenshensoft.widgetview.entity.WidgetMotionInfo;
import com.jenshensoft.widgetview.entity.WidgetPosition;

public interface OnWidgetMoveUpListener {

    void onMoveUp(View view, WidgetPosition widgetPosition, WidgetMotionInfo motionInfo);
}
