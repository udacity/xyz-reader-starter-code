package com.example.xyzreader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.widget.ImageView;

/**
 * Created by mike on 12/15/17.
 */

public class ArticleDetailMainActivity extends SingleFragmentActivity {

    private static final String ARG_VALUE_ID = "value_id";
    public static final String ARG_ITEM = "image_url";
    public static final String ARG_IMAGE_TRANSITION_NAME = "image_transition_name";
    public static Intent newIntent(Activity activity, Context packageContext, long id, ImageView sharedAnimation) {
        Intent intent = new Intent(packageContext, ArticleDetailMainActivity.class);
        intent.putExtra(ARG_VALUE_ID, id);
        intent.putExtra(ARG_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(sharedAnimation));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
//        return new ArticleListFragment();
        int id = getIntent().getIntExtra(ARG_VALUE_ID, 0);
        String sharedPreferences= getIntent().getStringExtra(ARG_IMAGE_TRANSITION_NAME);
//        return ArticleDetailActivity.newInstance(id, sharedPreferences);
//        return ArticleDetailActivity.newIntent(getApplicationContext(), 23, "mikeTest1" );
        return null;
    }
}
