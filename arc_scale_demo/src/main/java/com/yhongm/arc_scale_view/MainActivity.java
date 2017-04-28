package com.yhongm.arc_scale_view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yhongm.arc_scale_core.ArcScaleView;

public class MainActivity extends AppCompatActivity {
    ArcScaleView arcScaleView;
    TextView outputValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcScaleView = (ArcScaleView) findViewById(R.id.arc_scaleview);
        outputValue = (TextView) findViewById(R.id.tv_output_value);
        arcScaleView.setSelectScaleListener(value -> outputValue.setText("通过接口获取到的当前选择值:" + value));
    }
}
