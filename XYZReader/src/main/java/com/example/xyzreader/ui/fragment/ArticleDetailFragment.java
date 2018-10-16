package com.example.xyzreader.ui.fragment;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.xyzreader.R;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.presenter.ArticleDetailContract;
import com.example.xyzreader.ui.presenter.ArticleDetailsFragmentPresenter;
import com.example.xyzreader.ui.view.ArticleDetailActivity;
import com.example.xyzreader.ui.view.ArticleListActivity;

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

	View rootView;
	//	@BindView(R.id.sv_body)
//	NestedScrollView bodySV;
	@BindView(R.id.pb_details_fragment)
	ProgressBar progressBar;
	@BindView(R.id.tv_article_date)
	TextView dateTV;
	@BindView(R.id.tv_article_author)
	TextView authorTV;
	@BindView(R.id.tv_article_body)
	TextView bodyTV;

	private ArticleDetailContract.PresenterFragment presenter;
	private ArticleDetailContract.FragmentListener listener;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
	private SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
	private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);// Most time functions can only handle 1902 - 2037
	private boolean isCard = false;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(false);

		isCard = getResources().getBoolean(R.bool.detail_is_card);
		if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
			long mItemId = getArguments().getLong(ARG_ITEM_ID);
			Log.d(TAG, "Criando fragment: " + mItemId);
			presenter = new ArticleDetailsFragmentPresenter(requireContext(), this, mItemId);
		} else {
			throw new IllegalStateException("Não foi passado itemId");
		}
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

		Log.d(TAG, "Iniciando loader no frag com id: " + presenter.getArticleId());
		// noinspection deprecation
		requireActivity().getSupportLoaderManager().initLoader(ArticleDetailActivity.ARTICLE_BY_ID_FRAG_LOADER_ID + Integer.parseInt(String.valueOf(presenter.getArticleId())), null, presenter);
	}

	@Override
	public void onUpButtonFloorChanged(long itemId) {
		listener.onUpBuutonFloorChanged(itemId, getUpButtonFloor());
	}

	@Override
	public int getUpButtonFloor() {
		return 0;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
		ButterKnife.bind(this, rootView);
		bindView(null);
		Log.d(TAG, "Criando view para fragment com id: " + presenter.getArticleId());

		return rootView;
	}

	@Override
	public void updateStatusBar() {
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
	public void bindView(Cursor cursor) {
		dateTV.setMovementMethod(new LinkMovementMethod());
		bodyTV.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

		if (cursor != null) {
			Log.d(TAG, "Iniciando bind de id: " + presenter.getArticleId());
//			rootView.setAlpha(0);
//			rootView.animate().alpha(1);
			setProgressBarVisibility(false);
			Date publishedDate = parsePublishedDate(cursor);
			if (!publishedDate.before(START_OF_EPOCH.getTime())) {
				dateTV.setText(DateUtils.getRelativeTimeSpanString(
						publishedDate.getTime(),
						System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
						DateUtils.FORMAT_ABBREV_ALL).toString());

			} else { // If date is before 1902, just show the string
				dateTV.setText(outputFormat.format(publishedDate));
			}
			String authorName = cursor.getString(ArticleLoader.Query.AUTHOR);
			String authorText = String.format("by %s", authorName);
			authorTV.setText(authorText);
			String articleBody = cursor.getString(ArticleLoader.Query.BODY);
			bodyTV.setText(Html.fromHtml(articleBody.replaceAll("(\r\n|\n)", "<br />")));
		} else {
			Log.d(TAG, "Iniciando bind null");
			setProgressBarVisibility(true);
			dateTV.setText(getString(R.string.loading_info));
			authorTV.setText(getString(R.string.loading_info));
			bodyTV.setText(getString(R.string.loading_info));
		}
		Log.d(TAG, "Fim bind de id: " + presenter.getArticleId());
	}

	@Override
	public void setProgressBarVisibility(boolean visible) {
		if (visible) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

}
