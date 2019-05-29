package com.blunderer.materialdesignlibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.zcloud.R;

public class ToolbarDefault extends Toolbar {

    public ToolbarDefault(Context context) {
        this(context, null);
    }

    public ToolbarDefault(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToolbarDefault(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.mdl_toolbar_default, this, true);

        mToolbar = (androidx.appcompat.widget.Toolbar) getChildAt(0);
    }

    public androidx.appcompat.widget.Toolbar getToolbar() {
        return mToolbar;
    }

}
