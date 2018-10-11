package com.example.xyzreader.ui.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.presenter.ArticleDetailContract;
import com.example.xyzreader.ui.presenter.ArticleDetailsFragmentPresenter;
import com.example.xyzreader.ui.view.ArticleDetailActivity;
import com.example.xyzreader.ui.view.ArticleListActivity;
import com.example.xyzreader.ui.view.helper.ImageLoaderHelper;
import com.example.xyzreader.ui.view.xyz.DrawInsetsFrameLayout;
import com.example.xyzreader.ui.view.xyz.ObservableScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
		ArticleDetailContract.FragmentView {
	public static final String ARG_ITEM_ID = "item_id";
	private static final String TAG = "ArticleDetailFragment";
	private static final float PARALLAX_FACTOR = 1.25f;

	private ArticleDetailContract.PresenterFragment presenter;
	private ArticleDetailContract.FragmentListener listener;
	private View mRootView;
	private int mMutedColor = 0xFF333333;
	private ObservableScrollView mScrollView;
	private DrawInsetsFrameLayout mDrawInsetsFrameLayout;
	private ColorDrawable mStatusBarColorDrawable;

	private int mTopInset;
	private View mPhotoContainerView;
	private ImageView mPhotoView;
	private int mScrollY;
	private int mStatusBarFullOpacityBottom;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
	// Use default locale format
	private SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
	// Most time functions can only handle 1902 - 2037
	private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ArticleDetailFragment() {
	}

	public static ArticleDetailFragment newInstance(long itemId) {
		Bundle arguments = new Bundle();
		arguments.putLong(ARG_ITEM_ID, itemId);
		ArticleDetailFragment fragment = new ArticleDetailFragment();
		fragment.setArguments(arguments);

		return fragment;
	}

	static float progress(float v, float min, float max) {
		return constrain((v - min) / (max - min), 0, 1);
	}

	static float constrain(float val, float min, float max) {
		if (val < min) {
			return min;
		} else if (val > max) {
			return max;
		} else {
			return val;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
				R.dimen.detail_card_top_margin);
		setHasOptionsMenu(true);

		if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
			long mItemId = getArguments().getLong(ARG_ITEM_ID);
			presenter = new ArticleDetailsFragmentPresenter(requireContext(), this, mItemId);
		} else {
			throw new IllegalStateException("Não foi passado itemId");
		}
	}

	public ArticleDetailActivity getActivityCast() {
		return (ArticleDetailActivity) getActivity();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentActivity activity = getActivity();
		try {
			listener = (ArticleDetailContract.FragmentListener) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, "Activity que utilizar fragment deve implementar listener: " + e.getMessage());
			throw new IllegalStateException("Activity que utilizar fragment deve implementar listener", e);
		}

		// In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
		// the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
		// fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
		// we do this in onActivityCreated.
		//noinspection deprecation
		requireActivity().getSupportLoaderManager().initLoader(0, null, presenter);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
		mDrawInsetsFrameLayout = mRootView.findViewById(R.id.draw_insets_frame_layout);
		mDrawInsetsFrameLayout.setOnInsetsCallback(new DrawInsetsFrameLayout.OnInsetsCallback() {
			@Override
			public void onInsetsChanged(Rect insets) {
				mTopInset = insets.top;
			}
		});

		mScrollView = mRootView.findViewById(R.id.scrollview);
		mScrollView.setCallbacks(new ObservableScrollView.Callbacks() {
			@Override
			public void onScrollChanged() {
				mScrollY = mScrollView.getScrollY();
//				getActivityCast().onUpButtonFloorChanged(mItemId, ArticleDetailFragment.this); // TODO: 10/10/18 upbt
				mPhotoContainerView.setTranslationY((int) (mScrollY - mScrollY / PARALLAX_FACTOR));
				updateStatusBar();
			}
		});

		mPhotoView = mRootView.findViewById(R.id.photo);
		mPhotoContainerView = mRootView.findViewById(R.id.photo_container);

		mStatusBarColorDrawable = new ColorDrawable(0);

		mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				listener.shareArticle(view);
			}
		});

		bindView(null);
		updateStatusBar();

		return mRootView;
	}

	private void updateStatusBar() {
		int color = 0;
		if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
			float f = progress(mScrollY,
					mStatusBarFullOpacityBottom - mTopInset * 3,
					mStatusBarFullOpacityBottom - mTopInset);
			color = Color.argb((int) (255 * f),
					(int) (Color.red(mMutedColor) * 0.9),
					(int) (Color.green(mMutedColor) * 0.9),
					(int) (Color.blue(mMutedColor) * 0.9));
		}
		mStatusBarColorDrawable.setColor(color);
		mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
	}

	private Date parsePublishedDate(Cursor cursor) {
		try {
			String date = cursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			Log.e(TAG, ex.getMessage());
			Log.i(TAG, "passing today's date");

			return new Date();
		}
	}

	@Override
	public boolean isFragmentAdded() {
		return isAdded();
	}

	@Override
	public void bindView(Cursor mCursor) {
		if (mRootView == null) {
			return;
		}

		TextView titleView = mRootView.findViewById(R.id.article_title);
		TextView bylineView = mRootView.findViewById(R.id.article_byline);
		bylineView.setMovementMethod(new LinkMovementMethod());
		TextView bodyView = mRootView.findViewById(R.id.article_body);


		bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

		if (mCursor != null) {
			mRootView.setAlpha(0);
			mRootView.setVisibility(View.VISIBLE);
			mRootView.animate().alpha(1);
			titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
			Date publishedDate = parsePublishedDate(mCursor);
			if (!publishedDate.before(START_OF_EPOCH.getTime())) {
				bylineView.setText(Html.fromHtml(
						DateUtils.getRelativeTimeSpanString(
								publishedDate.getTime(),
								System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
								DateUtils.FORMAT_ABBREV_ALL).toString()
								+ " by <font color='#ffffff'>"
								+ mCursor.getString(ArticleLoader.Query.AUTHOR)
								+ "</font>"));

			} else {
				// If date is before 1902, just show the string
				bylineView.setText(Html.fromHtml(
						outputFormat.format(publishedDate) + " by <font color='#ffffff'>"
								+ mCursor.getString(ArticleLoader.Query.AUTHOR)
								+ "</font>"));

			}
			bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));
			ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
					.get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
						@Override
						public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
							Bitmap bitmap = imageContainer.getBitmap();
							if (bitmap != null) {
								Palette p = Palette.from(bitmap).maximumColorCount(12).generate();
								mMutedColor = p.getDarkMutedColor(0xFF333333);
								mPhotoView.setImageBitmap(imageContainer.getBitmap());
								mRootView.findViewById(R.id.meta_bar)
										.setBackgroundColor(mMutedColor);
								updateStatusBar();
							}
						}

						@Override
						public void onErrorResponse(VolleyError volleyError) {

						}
					});
		} else {
			mRootView.setVisibility(View.GONE);
			titleView.setText("N/A");
			bylineView.setText("N/A");
			bodyView.setText("N/A");
		}
	}

	// TODO: 10/10/18 upbt
	//	public int getUpButtonFloor() {
//		if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
//			return Integer.MAX_VALUE;
//		}
//
//		// account for parallax
//		return mIsCard
//				? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
//				: mPhotoView.getHeight() - mScrollY;
//	}

}
