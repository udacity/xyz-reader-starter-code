package com.example.xyzreader.ui.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.ui.fragment.ArticleDetailFragment;
import com.example.xyzreader.ui.presenter.ArticleDetailContract;
import com.example.xyzreader.ui.presenter.ArticleDetailsPresenter;
import com.example.xyzreader.ui.view.helper.ActivityHelper;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity implements ArticleDetailContract.View {

	public static final String ARTICLE_ID_EXTRA_PARAM_KEY = "article extra param key";

	private static final String TAG = ArticleDetailActivity.class.getSimpleName();

	private ViewPager mPager;
	private MyPagerAdapter mPagerAdapter;
	private View mUpButton;
	private ArticleDetailContract.Presenter presenter;
	private View rootView;
	private ProgressBar progressBar;

	// TODO: 10/10/18 verificar need
	private View mUpButtonContainer;
	private long mSelectedItemId;
	private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
	private int mTopInset;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		}
		setContentView(R.layout.activity_article_detail);

		presenter = new ArticleDetailsPresenter(this, this);

		rootView = findViewById(R.id.root_view);
		mPager = findViewById(R.id.pager);
		progressBar = findViewById(R.id.progress_bar);
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
				presenter.changeCursorPosition(position);
			}
		});

		mUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onSupportNavigateUp();
			}
		});

		// TODO: 10/10/18 validar uso de upBt
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
//				@Override
//				public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
//					view.onApplyWindowInsets(windowInsets);
//					mTopInset = windowInsets.getSystemWindowInsetTop();
//					mUpButtonContainer.setTranslationY(mTopInset);
//					updateUpButtonPosition();
//					return windowInsets;
//				}
//			});
//		}

		if (savedInstanceState == null) {
			if (getIntent() != null && getIntent().getData() != null) {
				long mStartId = ItemsContract.Items.getItemId(getIntent().getData());
				presenter.setStartId(mStartId);
			}
		}

		//noinspection deprecation
		getSupportLoaderManager().initLoader(0, null, presenter);
	}

	@Override
	public void startShareView(View view, Cursor cursor) {
		startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
				.setType("text/plain")
				.setText(cursor.getString(cursor.getColumnIndex(ItemsContract.Items.BODY)))
				.getIntent(), getString(R.string.action_share)));
	}

	@Override
	public void setPagerPos(int position) {
		mPager.setCurrentItem(position, false);
	}

	@Override
	public void updateUpBt(int position) {
//		updateUpButtonPosition();
	}

//	public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
//		if (itemId == mSelectedItemId) {
//			mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//			updateUpButtonPosition();
//		}
//	}

//	private void updateUpButtonPosition() {
//		int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
//		mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
//	}

	@Override
	public void showErrorMsg(String msg) {
		ActivityHelper.showErrorMsgWithSnack(rootView, msg);
	}

	@Override
	public void showSucessMsg(String msg) {
		ActivityHelper.showSucessMsgWithSnack(rootView, msg);
	}

	@Override
	public void setProgressBarVisibity(boolean visible) {
		if (visible) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void notifyViewPagerThatDataChanged() {
		mPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void shareArticle(View v) {
		presenter.shareArticle(v);
	}

	private class MyPagerAdapter extends FragmentStatePagerAdapter {
		MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			super.setPrimaryItem(container, position, object);
			// TODO: 10/10/18 upbt
//			ArticleDetailFragment fragment = null;
//			try {
//				fragment = (ArticleDetailFragment) object;
//			} catch (Exception e) {
//				Log.e(TAG, "Não foi possivel criar fragment de object: " + e.getMessage());
//			}
			//noinspection ConstantConditions
//			if (fragment != null) {
//				mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//				updateUpButtonPosition();
//			}
		}

		@Override
		public Fragment getItem(int position) {
			presenter.changeCursorPosition(position);

			return ArticleDetailFragment.newInstance(presenter.getArticleIdByCursor());
		}

		@Override
		public int getCount() {
			return presenter.getTotalInCursor();
		}
	}
}
