package com.example.xyzreader.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.ui.adapter.Adapter;
import com.example.xyzreader.ui.presenter.ArticleListContract;
import com.example.xyzreader.ui.presenter.ArticleListPresenter;
import com.example.xyzreader.ui.view.helper.ActivityHelper;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, ArticleListContract.View, Adapter.AdapterListener {
	@SuppressWarnings("unused")
	private static final String TAG = ArticleListActivity.class.toString();

	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;

	private boolean mIsRefreshing = false;
	private View rootLayout;
	private ProgressBar progressBar;

	private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
				mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
				updateRefreshingUI();
			}
		}
	};

	private ArticleListContract.Presenter presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);

		Toolbar mToolbar = findViewById(R.id.toolbar);
		rootLayout = findViewById(R.id.main_root);
		progressBar = findViewById(R.id.pb_details_fragment);
		mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
		mRecyclerView = findViewById(R.id.recycler_view);

		ActivityHelper.configureActionBar(this, mToolbar);
		presenter = new ArticleListPresenter(this, this);
		//noinspection deprecation
		getSupportLoaderManager().initLoader(0, null, this);

		if (savedInstanceState == null) {
			refresh();
		} else {
			presenter.restoreSavedState(savedInstanceState);
		}
	}

	private void refresh() {
		startService(new Intent(this, UpdaterService.class));
	}

	@Override
	protected void onStart() {
		super.onStart();
		presenter.loadArticleList(mRefreshingReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mRefreshingReceiver);
	}

	private void updateRefreshingUI() {
		mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return ArticleLoader.newAllArticlesInstance(this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
		presenter.onLoaderFinish(cursorLoader, cursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mRecyclerView.setAdapter(null);
	}

	@Override
	public void showErrorMsg(String msg) {
		ActivityHelper.showErrorMsgWithSnack(rootLayout, msg);
	}

	@Override
	public void showSucessMsg(String msg) {
		ActivityHelper.showSucessMsgWithSnack(rootLayout, msg);
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
	public void showArticleDetailsView(Article article) {
		Intent intent = new Intent(Intent.ACTION_VIEW, ItemsContract.Items.buildItemUri(article.getId()));

		startActivity(intent);
	}

	@Override
	public void setArticleListPositionTo(int position) {
		if (mRecyclerView.getItemDecorationCount() > position) {
			mRecyclerView.setVerticalScrollbarPosition(position);
		} else {
			Log.w(TAG, "Tentando fazer scrool em RV sem o total de itens no adapter: " + position);
		}
	}

	@Override
	public void createAdapter(Cursor cursor) {
		Adapter adapter = new Adapter(this, cursor, this);
		adapter.setHasStableIds(true);
		mRecyclerView.setAdapter(adapter);

		int columnCount = getResources().getInteger(R.integer.list_column_count);
		StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(sglm);
	}

	@Override
	public void selectArticle(int position, Long id) {
		presenter.selectArticle(position, id);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (presenter != null) {
			presenter.savePositionState(outState, mRecyclerView.getVerticalScrollbarPosition());
		}
	}
}
