package com.example.xyzreader.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.util.Log;
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
		return ArticleLoader.newInstanceForItemId(context, itemId);
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
		if (mCursor != null && !mCursor.moveToFirst()) {
			Log.e(TAG, "Error reading item detail cursor");
			mCursor.close();
			mCursor = null;
		}

		view.bindView(mCursor);
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mCursor = null;
		view.bindView(null);
	}
}
