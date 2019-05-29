package com.zcloud;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.salesforce.androidsdk.rest.RestClient;

import utilities.ExceptionHandler;
import fragment.ReportsFragment;

public class ReportsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    @Override
    public void onResume(RestClient client) {
    }

    @Override
    public int getNotificationVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public int getMenuVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public int getBackVisibillity() {
        return View.GONE;
    }

    @Override
    public String getHeaderTitle() {
        return "Reports";
    }

    @Override
    public Fragment GetFragment() {
        Fragment fragment = ReportsFragment.newInstance("Reports");
        return fragment;
    }
}
