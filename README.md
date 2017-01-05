# WidgetView
This library contains a WidgetView and WidgetContainerLayout for working with custom widgets.

## WidgetView
A viewgroup for scaling and dragging all types of views.
## Demo
<img src="http://i.imgur.com/yr0Ekzl.gif" height="683" width="384">

## WidgetContainerLayout
A viewgroup for storing WidgetViews on the grid.
## Demo
<img src="http://i.imgur.com/iIraYZg.gif" height="683" width="384">

## Download
Add to your module's build.gradle:
```java
allprojects {
    repositories {
       maven { url 'https://dl.bintray.com/jenshen1992/ua.jenshensoft/' }
    }
}
```

and:
```java
dependencies {    
    compile 'ua.jenshensoft:widgetview:0.65'
}
```
## How to use WidgetView
To use the WidgetView on your app, add the following code to your layout:

```xml
 <com.jenshensoft.widgetview.WidgetView
        android:layout_width="140dp"
        android:layout_height="140dp"
        app:widgetView_pointWidth="140dp"
        app:widgetView_pointHeight="140dp"
        app:widgetView_pointIcon="@drawable/point"
        app:widgetView_dragAndDrop_byLongClick="false">

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/cat"/>

    </com.jenshensoft.widgetview.WidgetView>
```    
You can customize WidgetView with:

- app:widgetView_pointWidth: a width for point on corners (you can scale view by this points)
- app:widgetView_pointHeight:  a height for point on corners (you can scale view by this points)
- app:widgetView_pointIcon: set a custom icon for points on corners
- app:widgetView_dragAndDrop_byLongClick: you can drag your view after a long click (it's disabled by default, you can drag the view with out a long click)

## How to use WidgetContainerLayout
To use the WidgetContainerLayout on your app, add the following code to your layout:
```xml
<com.jenshensoft.widgetview.WidgetContainerLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.jenshensoft.widgetview.WidgetView
            android:layout_width="140dp"
            android:layout_height="140dp"
            app:widgetView_dragAndDrop_byLongClick="false"
            android:background="@color/colorPrimary">

             <ImageView
                  android:layout_width="match_parent"
                  android:layout_height="match_parent"
                  android:background="@color/colorAccent" />

        </com.jenshensoft.widgetview.WidgetView>
        
        <com.jenshensoft.widgetview.WidgetView
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:widgetView_corner_bottomLeft_columnLine="1"
            app:widgetView_corner_bottomLeft_rowLine="3"
            app:widgetView_corner_bottomRight_columnLine="4"
            app:widgetView_corner_bottomRight_rowLine="3"
            app:widgetView_corner_topLeft_columnLine="1"
            app:widgetView_corner_topLeft_rowLine="2"
            app:widgetView_corner_topRight_columnLine="4"
            app:widgetView_corner_topRight_rowLine="2"
            app:widgetView_dragAndDrop_byLongClick="false">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:contentDescription="@null"
                android:src="@drawable/cat" />

        </com.jenshensoft.widgetview.WidgetView>
    </com.jenshensoft.widgetview.WidgetContainerLayout>
```
You can customize WidgetContainerLayout with:

- app:widgetContainer_columnCount: the count of column lines
- app:widgetContainer_rowCount: the count of row lines
- app:widgetContainer_linesColor: you can set a color for lines
- app:widgetContainer_autoConnect_AvailabilityZone: you can specify the distance when widget can find a points for fixed position after you move up your finger.
- app:widgetContainer_enableTrash: you can enable the trash for delete same views by drag on the trash dislocation(by default enable)
- app:widgetContainer_deletePanelGravity: you can set the trash position on the WidgetContainerLayout

You can specify start widget position from code or your xml layout by this way:
From code:
```java

        WidgetView widgetView = ...
        WidgetPosition widgetPosition = widgetView.getWidgetPosition();
        widgetPosition.setTopLeftColumnLine(1);
        widgetPosition.setTopLeftRowLine(2);
        widgetPosition.setTopRightColumnLine(4);
        widgetPosition.setTopRightRowLine(2);
        widgetPosition.setBottomLeftColumnLine(1);
        widgetPosition.setBottomLeftRowLine(3);
        widgetPosition.setBottomRightColumnLine(4);
        widgetPosition.setBottomRightRowLine(3);
        
```
From xml:
```xml
 <com.jenshensoft.widgetview.WidgetView
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:widgetView_corner_bottomLeft_columnLine="1"
            app:widgetView_corner_bottomLeft_rowLine="3"
            app:widgetView_corner_bottomRight_columnLine="4"
            app:widgetView_corner_bottomRight_rowLine="3"
            app:widgetView_corner_topLeft_columnLine="1"
            app:widgetView_corner_topLeft_rowLine="2"
            app:widgetView_corner_topRight_columnLine="4"
            app:widgetView_corner_topRight_rowLine="2"
            app:widgetView_dragAndDrop_byLongClick="false">

            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:contentDescription="@null"
                android:src="@drawable/cat" />

        </com.jenshensoft.widgetview.WidgetView>
```   
You need to specify all of points!
## License
```
Copyright 2016 BlueLine Labs, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
