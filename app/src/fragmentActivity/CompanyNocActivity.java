package fragmentActivity;


import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.zcloud.R;
import utilities.ExceptionHandler;
import fragment.NOC.CompanyNocMainFragment;

public class CompanyNocActivity extends BaseFragmentActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.noc);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, CompanyNocMainFragment.newInstance("CompanyNocMainFragment", this))
                .commit();
    }
}