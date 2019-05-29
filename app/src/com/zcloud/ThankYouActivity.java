package com.zcloud;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.zcloud.R;

import com.zcloud.R;
import fragment.GenericThankYouFragment;

/**
 * Created by Abanoub Wagdy on 10/25/2015.
 */
public class ThankYouActivity extends FragmentActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company_info_inner_fragment);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, GenericThankYouFragment.newInstance(getIntent().getExtras().getString("caseNumber"), null, null), "Thank You")
                .commitAllowingStateLoss();
    }
}
