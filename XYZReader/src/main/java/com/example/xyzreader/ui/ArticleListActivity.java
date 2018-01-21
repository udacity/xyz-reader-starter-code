package com.example.xyzreader.ui;

import android.support.v4.app.Fragment;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        //return new ArticleListFragment();
        return null;
    }
}
