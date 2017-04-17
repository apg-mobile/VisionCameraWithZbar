package com.example.example.visionCameraWithZbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Vijaya.Karri on 17/4/2017.
 */

@EActivity(R.layout.activity_result)
public class ResultActivity extends AppCompatActivity {

    @ViewById
    protected TextView tvResult;

    @Extra
    @InstanceState
    protected String result;

    @AfterViews
    protected void init(){
        tvResult.setText(result);
    }
}
