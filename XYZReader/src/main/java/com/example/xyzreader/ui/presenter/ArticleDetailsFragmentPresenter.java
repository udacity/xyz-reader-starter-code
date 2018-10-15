package com.example.xyzreader.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import com.example.xyzreader.data.loader.ArticleLoader;

public class ArticleDetailsFragmentPresenter implements ArticleDetailContract.PresenterFragment {
	private static final String TAG = ArticleDetailsFragmentPresenter.class.toString();

	private Context context;
	private ArticleDetailContract.FragmentView view;
	private Cursor mCursor;
	private long itemId;

	public ArticleDetailsFragmentPresenter(Context context, ArticleDetailContract.FragmentView view, long itemId) {
		this.context = context;
		this.view = view;
		this.itemId = itemId;
	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
		return ArticleLoader.newInstanceForItemId(context, itemId, this);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
		if (!view.isFragmentAdded()) {
			if (cursor != null) {
				cursor.close();
			}
			return;
		}

		mCursor = cursor;
		cursor.moveToFirst();
		view.bindView(mCursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mCursor.close();
		mCursor = null;
		view.bindView(null);
	}

	@Override
	public void onScroolChanged(int mScrollY) {
	}

	@Override
	public long getArticleId() {
		return itemId;
	}

	@Override
	public void onStartLoad() {
		view.setProgressBarVisibility(true);
	}

	@Override
	public void onFinishLoad() {
		view.setProgressBarVisibility(false);
	}
}
