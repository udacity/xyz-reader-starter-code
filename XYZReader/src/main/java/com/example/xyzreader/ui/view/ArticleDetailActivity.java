package com.example.xyzreader.ui.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.fragment.ArticleDetailFragment;
import com.example.xyzreader.ui.presenter.ArticleDetailContract;
import com.example.xyzreader.ui.presenter.ArticleDetailsPresenter;
import com.example.xyzreader.ui.view.helper.ActivityHelper;
import com.example.xyzreader.ui.view.helper.ImageLoaderHelper;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity implements ArticleDetailContract.View {

	private static final String TAG = ArticleDetailActivity.class.getSimpleName();

	private MyPagerAdapter mPagerAdapter;
	private ArticleDetailContract.Presenter presenter;

	@BindView(R.id.root_view)
	View rootView;
	@BindView(R.id.collapsingBar)
	CollapsingToolbarLayout collapsingToolbarLayout;
	@BindView(R.id.toolbar)
	Toolbar toobar;
	//	@BindView(R.id.tv_article_title)
//	TextView titleTV;
	@BindView(R.id.iv_article)
	ImageView articleIV;
	@BindView(R.id.vp_article)
	ViewPager articleVP;
	@BindView(R.id.progress_bar)
	ProgressBar progressBar;
	@BindView(R.id.fab_share)
	FloatingActionButton shareFAB;

	@Override
	public void createPagerAdapter() {
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		articleVP.setAdapter(mPagerAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			setFullScreenLoliPop();
//		}
		setContentView(R.layout.activity_article_detail);
		presenter = new ArticleDetailsPresenter(this, this);
		ButterKnife.bind(this);

		if (savedInstanceState == null) {
			if (getIntent() != null && getIntent().getData() != null) {
				long mStartId = ItemsContract.Items.getItemId(getIntent().getData());
				presenter.setStartId(mStartId);
			} else {
				throw new IllegalStateException("Não foi passado itemId");
			}
		}

		ActivityHelper.configureActionBar(this, toobar);
		ActivityHelper.configureHomeButton(this);
	}

	private void setFullScreenLoliPop() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
	}

	@Override
	protected void onStart() {
		super.onStart();
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
	public void bindView(Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(ItemsContract.Items.TITLE));
//		titleTV.setText(title);
		ImageLoaderHelper.getInstance(this).getImageLoader()
				.get(cursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
					@Override
					public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
						Bitmap bitmap = imageContainer.getBitmap();
						if (bitmap != null) {
							articleIV.setImageBitmap(bitmap);
						}
					}

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						articleIV.setImageDrawable(getResources().getDrawable(R.drawable.empty_detail));
					}
				});
		collapsingToolbarLayout.setTitle(title);
		collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
		collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
		shareFAB.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				presenter.shareArticle(view);
			}
		});
	}

	@Override
	public void setPagerPos(int position) {
		articleVP.setCurrentItem(position, false);
	}

	@Override
	public void updateUpBt(int position) {
		updateUpButtonPosition();
	}

	private void updateUpButtonPosition() {

	}

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
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onUpBuutonFloorChanged(long itemId, int upButtonFloor) {

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
