package com.blunderer.materialdesignlibrary.models;

import androidx.fragment.app.Fragment;

public class NavigationDrawerListItemTopFragment extends ListItem {

    private Fragment mFragment;

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

}
