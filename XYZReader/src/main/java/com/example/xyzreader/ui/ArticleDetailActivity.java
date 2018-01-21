package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_VALUE_ID = "value_id";
    public static final String ARG_ITEM = "image_url";
    public static final String ARG_IMAGE_TRANSITION_NAME = "image_transition_name";

    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;
    private String imageTransitionName;
    private View mRootView;

    public static Intent newIntent(Context packageContent, long id, String sharedPreferences) {
        Log.d("MIKE Activity intent:::", String.valueOf(id));
        Intent intent = new Intent(packageContent, ArticleDetailActivity.class);
        intent.putExtra(ARG_VALUE_ID, id);
        intent.putExtra(ARG_IMAGE_TRANSITION_NAME, sharedPreferences);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                        getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
//        }
//        setSharedElementReturnTransition(null);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                Log.d("MIKE", "trigger an exception");
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("MIKE actdet ", "on createLoader MIKE4 this should not be need it");
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d("MIKE act ", "onLoadFinished MIKE5");
        mCursor = cursor;
        mPagerAdapter.swapCursor(cursor);

        // Select the start ID
        if (mStartId >= 0) {
            mCursor.moveToFirst();
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    //  changeViews();
                    mPager.setCurrentItem(mCursor.getPosition(), false);
                    break;
                }
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d("MIKE DetAct", "detAct MIKE7");
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
        Log.d("MIKE ", " onUpButtonFloorChanged MIKE8");
        if (itemId == mSelectedItemId) {
            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
            // updateUpButtonPosition();
        }
    }

    private void updateUpButtonPosition() {
        Log.d("MIKE ACti", "updateUpButtonPosition MIKE3");
        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private Cursor mCursor;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("MIKE act getITEM", "MIKE10");
            if (mCursor == null) {
                Log.d("MIKE", "getItem mCursor == null");
                return null;
            }

            if (mCursor.moveToPosition(position)) {
                return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID), imageTransitionName);
            }
            return null;
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }
}
