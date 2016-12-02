package com.jenshensoft.widgetview.sample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jenshen.widgetview.sample.R;
import com.jenshensoft.widgetview.WidgetContainerLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WidgetContainerLayout widgetContainer = (WidgetContainerLayout) findViewById(R.id.container);
        View view = new Button(this);
        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
       // widgetContainer.addViewForWidget(view);
    }
}
