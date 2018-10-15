package com.example.xyzreader.ui.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.data.model.Article;

public class ArticleListPresenter implements ArticleListContract.Presenter {


	private static final String SAVED_POSITION_KEY = "saved position";
	private ArticleListContract.View view;
	private Context context;
	private int savedPosition = 0;

	public ArticleListPresenter(ArticleListContract.View view, Context context) {
		this.view = view;
		this.context = context;
	}

	@Override
	public void loadArticleList(BroadcastReceiver mRefreshingReceiver) {
		context.registerReceiver(mRefreshingReceiver, new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
	}

	@Override
	public void selectArticle(int position, Long id) {
		Article article = new Article();
		article.setId(id);
		view.showArticleDetailsView(article);
	}

	@Override
	public void restoreSavedState(Bundle savedInstanceState) {
		int savedPosition = savedInstanceState.getInt(SAVED_POSITION_KEY, -1);
		if (savedPosition >= 0) {
			this.savedPosition = savedPosition;
		}
	}


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return ArticleLoader.newAllArticlesInstance(context, this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> cursorLoader, Cursor cursor) {
		view.createAdapter(cursor);
		view.setRefreshState(false);
		view.setProgressBarVisibity(false);
		if (savedPosition > 0) {
			view.setArticleListPositionTo(savedPosition);
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		view.destroyList();
	}

	@Override
	public void savePositionState(Bundle outState, int verticalScrollbarPosition) {
		outState.putInt(SAVED_POSITION_KEY, verticalScrollbarPosition);
	}

	@Override
	public void onStartLoad() {
		view.setProgressBarVisibity(true);
	}

	@Override
	public void onFinishLoad() {
		view.setProgressBarVisibity(false);
	}
}
