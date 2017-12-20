package com.example.xyzreader.ui;



//import android.app.LoaderManager;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends Fragment
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

    public static Intent newIntent(Context packageContent, long id, String sharedPreferences) {
        Log.d("MIKE Activity intent:::", String.valueOf(id));
        Intent intent = new Intent(packageContent, ArticleDetailActivity.class);
        intent.putExtra(ARG_VALUE_ID, id);
        intent.putExtra(ARG_IMAGE_TRANSITION_NAME, sharedPreferences);
        return intent;
    }

    public static ArticleDetailActivity newInstance( long recipeName, String sharedPreferences) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VALUE_ID, recipeName);
        args.putSerializable(ARG_IMAGE_TRANSITION_NAME, sharedPreferences);
        Log.d("MIKE newInstance", sharedPreferences);

        ArticleDetailActivity fragment = new ArticleDetailActivity();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            postponeEnterTransition();
//            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
//        }
//        setSharedElementReturnTransition(null);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStartId = (long) getArguments()
                .getLong(ARG_VALUE_ID);
        Log.d("MIKE ADA:::ID: ", String.valueOf(mStartId));

        imageTransitionName = getArguments().getString(ARG_IMAGE_TRANSITION_NAME);

        Log.d("MIKE ADA:::: ", "sharedTransitions " +imageTransitionName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        getLoaderManager().initLoader(0, null, this);
//        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
//        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        if (savedInstanceState == null) {
            if (getActivity().getIntent() != null && getActivity().getIntent().getData() != null) {
                Log.d("MIKE", "trigger an exception");
                mStartId = ItemsContract.Items.getItemId(getActivity().getIntent().getData());
                mSelectedItemId = mStartId;
            }
        }

//        setSharedElementReturnTransition(null);
        return inflater.inflate(R.layout.activity_article_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
//        mPager.setOffscreenPageLimit(3);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                        getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d("MIKE actdet", "MIKE1");
                mUpButton.animate()
                        .alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
                        .setDuration(300);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }

                Log.d("MIKE act ", "onPageSelected MIKE2");
                mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                updateUpButtonPosition();
            }
        });

        mUpButtonContainer = view.findViewById(R.id.up_container);

        mUpButton = view.findViewById(R.id.action_up);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO fix this next line that is commented because it cause an error
//                onSupportNavigateUp();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    view.onApplyWindowInsets(windowInsets);
                    mTopInset = windowInsets.getSystemWindowInsetTop();
                    mUpButtonContainer.setTranslationY(mTopInset);
                    updateUpButtonPosition();
                    return windowInsets;
                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("MIKE actdet ", "on createLoader MIKE4 this should not be need it");
        return ArticleLoader.newAllArticlesInstance(getContext());
    }

//    @Override
//    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
//
//    }

//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        Log.d("MIKE act ", "onLoadFinished MIKE5");
        mCursor = cursor;
        mPagerAdapter.swapCursor(cursor);
        //mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                Log.d("MIKE detAct", " - - - - - - - - - - - - - - - - - - >");
                Log.d("MIKE detAct", " ... checking!");
                Log.d("MIKE detAct", Long.toString(mCursor.getLong(ArticleLoader.Query._ID)));
                Log.d("MIKE detAct", mCursor.getString(ArticleLoader.Query.TITLE));
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {

                    final int position = mCursor.getPosition();
                    Log.d("MIKE detact", "move PAGER MIKE6");
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
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
            updateUpButtonPosition();
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
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                //mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
                updateUpButtonPosition();
            }
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
