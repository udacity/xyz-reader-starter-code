package com.example.xyzreader.ui.view;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.fragment.ArticleDetailFragment;
import com.example.xyzreader.ui.view.helper.ActivityHelper;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
		implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String ARTICLE_ID_EXTRA_PARAM_KEY = "article extra param key";

	private static final String TAG = ArticleDetailActivity.class.getSimpleName();

	private Cursor mCursor;
	private long mStartId;

	private long mSelectedItemId;
	private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
	private int mTopInset;

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

		//noinspection deprecation
		getSupportLoaderManager().initLoader(0, null, this);

		mPager = findViewById(R.id.pager);
		Toolbar mToolbar = findViewById(R.id.toolbar);
		mUpButtonContainer = findViewById(R.id.toolbar_container);
		mUpButton = findViewById(R.id.action_up);

		ActivityHelper.configureActionBar(this, mToolbar);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageMargin(
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
		mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));

		//noinspection deprecation
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
				super.onPageScrollStateChanged(state);
				mUpButton.animate()
						.alpha((state == ViewPager.SCROLL_STATE_IDLE) ? 1f : 0f)
						.setDuration(300);
			}

			@Override
			public void onPageSelected(int position) {
				if (mCursor != null) {
					mCursor.moveToPosition(position);
					mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
					updateUpButtonPosition();
				}
			}
		});

		mUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onSupportNavigateUp();
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

		if (savedInstanceState == null) {
			if (getIntent() != null && getIntent().getData() != null) {
				mStartId = ItemsContract.Items.getItemId(getIntent().getData());
				mSelectedItemId = mStartId;
			}
		}

		//noinspection deprecation
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return ArticleLoader.newAllArticlesInstance(this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
		mCursor = cursor;
		mPagerAdapter.notifyDataSetChanged();

		// Select the start ID
		if (mStartId > 0) {
			mCursor.moveToFirst();
			// TODO: optimize
			while (!mCursor.isAfterLast()) {
				if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
					final int position = mCursor.getPosition();
					mPager.setCurrentItem(position, false);
					break;
				}
				mCursor.moveToNext();
			}
			mStartId = 0;
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> cursorLoader) {
		mCursor = null;
		mPagerAdapter.notifyDataSetChanged();
	}

	public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
		if (itemId == mSelectedItemId) {
			mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
			updateUpButtonPosition();
		}
	}

	private void updateUpButtonPosition() {
		int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
		mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
		MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			super.setPrimaryItem(container, position, object);
			ArticleDetailFragment fragment = null;
			try {
				fragment = (ArticleDetailFragment) object;
			} catch (Exception e) {
				Log.e(TAG, "Não foi possivel criar fragment de object: " + e.getMessage());
			}
			//noinspection ConstantConditions
			if (fragment != null) {
				mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
				updateUpButtonPosition();
			}
		}

		@Override
		public Fragment getItem(int position) {
			mCursor.moveToPosition(position);

			return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
		}

		@Override
		public int getCount() {
			return (mCursor != null) ? mCursor.getCount() : 0;
		}
	}
}
