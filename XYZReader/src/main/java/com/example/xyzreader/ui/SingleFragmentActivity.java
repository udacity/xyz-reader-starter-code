package com.example.xyzreader.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.xyzreader.R;

/**
 * Created by mike on 12/12/17.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();
    private ImageView mySharedImageView;
    public static final String ARG_IMAGE_TRANSITION_NAME = "image_transition_name";

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        Log.d("MIKE", "single fragment activity onCreateMIKE onCreate");
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            try {
                String sharedAnimation = fragment.getArguments().getString("image_transition_name");
                Log.d("MIKE sharedTransition", "From SingleFragmentActivity XXX" + sharedAnimation);
            } catch (Exception e) {
                Log.d("MIKE EXCEPTION", e.toString());
            }
            fm.beginTransaction()
//                    .addSharedElement(sharedAnimation, ViewCompat.getTransitionName(sharedAnimation))
//                    .addSharedElement()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.main_activity;
    }
}