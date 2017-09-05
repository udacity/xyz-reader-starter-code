package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ArticleUtils;
import com.example.xyzreader.pojo.Article;

import java.util.ArrayList;

import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_CLICKED_IMAGE_URL;
import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_INITIAL_POSITION;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Log tag
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    // Member variables
    private int mInitialPosition;
    private String mInitialPhotoUrl;
    private ArrayList<Article> mArticles;

    // views
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    private View mUpButtonContainer;
    private View mUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        setContentView(R.layout.activity_article_detail);

        // start loader to grab all article data
        getLoaderManager().initLoader(0, null, this);

        // Grab data from received intent
        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            mInitialPosition = receivedIntent.getIntExtra(EXTRA_INITIAL_POSITION, 0);
            // mArticles = receivedIntent.getParcelableArrayListExtra(EXTRA_ARTICLES);
            mInitialPhotoUrl = receivedIntent.getStringExtra(EXTRA_CLICKED_IMAGE_URL);
        }

        // TODO: Update initial photo src

        // TODO: Hide top status bar

        mUpButtonContainer = findViewById(R.id.up_container);
        mUpButton = findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        // TODO: Do I need an onpagechangelistener to change the item..?
//        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                super.onPageScrollStateChanged(state);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (mCursor != null) {
//                    mCursor.moveToPosition(position);
//                }
//            }
//        });
        mPager.setCurrentItem(mInitialPosition);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "Loader started for all articles");
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "Loader finished.");

        // store articles in arraylist
        mArticles = ArticleUtils.generateArticlesFromCursor(cursor);

        mPagerAdapter.notifyDataSetChanged();

        mPager.setCurrentItem(mInitialPosition, false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if (mArticles != null) { return mArticles.size(); }
            else return 0;
        }

        @Override
        public Fragment getItem(int position) {
            // COMPLETED: get article object
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(mArticles.get(position));
            return fragment;
        }

    }
}
