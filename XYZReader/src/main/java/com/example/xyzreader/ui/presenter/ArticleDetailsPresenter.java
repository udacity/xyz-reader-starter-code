package com.example.xyzreader.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.view.ArticleDetailActivity;

public class ArticleDetailsPresenter implements ArticleDetailContract.Presenter {
	private static final boolean PROGRESS_BAR_VISIBLE = true;
	private static final boolean PROGRESS_BAR_INVISIBLE = false;
	private Context context;
	private ArticleDetailContract.View view;
	private Cursor cursor;
	private long curretItemId;
	private Cursor allArticlesCursor;
	private boolean findSelectedPosition = true;

	public ArticleDetailsPresenter(Context context, ArticleDetailContract.View view) {
		this.context = context;
		this.view = view;
	}

	@Override
	public void restoreSavedState(Bundle savedInstanceState) {

	}

	@Override
	public void savePositionState(Bundle outState, int verticalScrollbarPosition) {

	}

	@Override
	public void shareArticle(View view) {
		this.view.startShareView(view, cursor);
	}

	@Override
	public boolean onPageChange(int position) {
		if (allArticlesCursor != null) {
			boolean moved = allArticlesCursor.moveToPosition(position);
			curretItemId = allArticlesCursor.getLong(ArticleLoader.Query._ID);
			//noinspection deprecation

			LoaderManager supportLoaderManager = ((AppCompatActivity) context).getSupportLoaderManager();
			if (supportLoaderManager.getLoader(ArticleDetailActivity.ARTICLE_BY_ID_LOADER_ID) == null) {
				supportLoaderManager.initLoader(ArticleDetailActivity.ARTICLE_BY_ID_LOADER_ID, null, this);
			} else {
				supportLoaderManager.restartLoader(ArticleDetailActivity.ARTICLE_BY_ID_LOADER_ID, null, this);
			}

			return moved;
		}

		return false;
	}

	@Override
	public long getArticleIdByCursor() {
		return cursor.getLong(ArticleLoader.Query._ID);
	}

	@Override
	public void setStartId(long mStartId) {
		this.curretItemId = mStartId;
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
		switch (i) {
			case ArticleDetailActivity.ARTICLE_BY_ID_LOADER_ID:
				ArticleLoader articleLoader = ArticleLoader.newInstanceForItemId(context, curretItemId, this);
				articleLoader.commitContentChanged();
				return articleLoader;
		}

		return ArticleLoader.newAllArticlesInstance(context, this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		switch (loader.getId()) {
			case ArticleDetailActivity.ARTICLE_BY_ID_LOADER_ID:
				this.cursor = cursor;
				this.cursor.moveToFirst();
				view.bindView(cursor);
				break;
			case ArticleDetailActivity.ALL_ARTICLES_LOADER_ID:
				this.allArticlesCursor = cursor;
				cursor.moveToFirst();
				int positionFound = -1;
				if (findSelectedPosition) { // Primeira vez
					positionFound = findCurrentItemPosition(cursor);
					if (positionFound == 0) {
						view.bindView(cursor);
					}
					findSelectedPosition = false;
				}
				view.createPagerAdapter(cursor);
				if (positionFound > 0) {
					view.setPagerPos(positionFound);
				}
				break;
		}
	}

	private int findCurrentItemPosition(Cursor cursor) {
		while (!cursor.isAfterLast()) {
			if (cursor.getLong(ArticleLoader.Query._ID) == curretItemId) {
				return cursor.getPosition();
			}
			cursor.moveToNext();
		}

		return -1;
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		if (loader.getId() == ArticleDetailActivity.ALL_ARTICLES_LOADER_ID) {
			view.notifyViewPagerThatDataChanged();
		} else {
			view.swapCursor(null);
			cursor = null;
		}
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
