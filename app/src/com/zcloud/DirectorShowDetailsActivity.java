package com.zcloud;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import com.zcloud.BaseActivity;
import com.zcloud.R;
import fragment.DirectorsShowDetailsFragment;
import model.Directorship;

/**
 * Created by Abanoub Wagdy on 9/17/2015.
 */
public class DirectorShowDetailsActivity extends BaseActivity {


    private Directorship _directorship;

    @Override
    public int getNotificationVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public int getMenuVisibillity() {
        return View.GONE;
    }

    @Override
    public int getBackVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public String getHeaderTitle() {
        return "Directors";
    }

    @Override
    public Fragment GetFragment() {
        Gson gson = new Gson();
        _directorship = gson.fromJson(getIntent().getExtras().getString("object"), Directorship.class);
        Fragment fragment = DirectorsShowDetailsFragment.newInstance("DirectorsShowDetailsFragment",_directorship);
        return fragment;
    }
}
