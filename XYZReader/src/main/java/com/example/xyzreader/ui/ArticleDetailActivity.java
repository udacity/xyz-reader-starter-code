package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.pojo.Article;

import java.util.ArrayList;

import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_ARTICLES;
import static com.example.xyzreader.ui.ArticleListActivity.EXTRA_INITIAL_POSITION;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();

    // Member variables
    private int mInitialPosition;
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

        // Grab data from received intent
        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            mInitialPosition = receivedIntent.getIntExtra(EXTRA_INITIAL_POSITION, 0);
            mArticles = receivedIntent.getParcelableArrayListExtra(EXTRA_ARTICLES);
        }

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

        // TODO: Do I need to notify that the dataset changed initially?
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
            // TODO: get article object
            ArticleDetailFragment fragment = ArticleDetailFragment.newInstance(mArticles.get(position));
            return fragment;
        }

    }
}
