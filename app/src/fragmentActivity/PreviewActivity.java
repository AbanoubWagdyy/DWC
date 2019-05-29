package fragmentActivity;

import com.zcloud.BaseActivity;
import com.zcloud.R;
import android.view.View;

import androidx.fragment.app.Fragment;

import fragment.PreviewFragment;

/**
 * Created by Abanoub Wagdy on 9/9/2015.
 */
public class PreviewActivity extends BaseActivity {

    @Override
    public int getNotificationVisibillity() {
        return View.GONE;
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
        return "Preview";
    }

    @Override
    public Fragment GetFragment() {
        Fragment fragment = PreviewFragment.newInstance(getApplicationContext(), getIntent().getExtras().getString("object"), getIntent().getExtras().getString("type"));
        return fragment;
    }
}
