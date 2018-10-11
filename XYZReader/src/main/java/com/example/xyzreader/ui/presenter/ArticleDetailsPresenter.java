package com.example.xyzreader.ui.presenter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;
import com.example.xyzreader.data.loader.ArticleLoader;

public class ArticleDetailsPresenter implements ArticleDetailContract.Presenter {
	private static final boolean PROGRESS_BAR_VISIBLE = true;
	private static final boolean PROGRESS_BAR_INVISIBLE = false;
	private Context context;
	private ArticleDetailContract.View view;
	private Cursor mCursor;
	private long mStartId;

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
		this.view.startShareView(view, mCursor);
	}

	@Override
	public int getTotalInCursor() {
		if (mCursor != null) {
			return mCursor.getCount();
		} else {
			return 0;
		}
	}

	@Override
	public long getArticleIdByCursor() {
		return mCursor.getLong(ArticleLoader.Query._ID);
	}

	@Override
	public void setStartId(long mStartId) {
		this.mStartId = mStartId;
	}

	@Override
	public void changeCursorPosition(int position) {
		if (mCursor != null) {
			mCursor.moveToPosition(position);
//			mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID); // TODO: 10/10/18 verificar upbt
			view.updateUpBt(position);
		}
	}


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
		view.setProgressBarVisibity(PROGRESS_BAR_VISIBLE);
		return ArticleLoader.newAllArticlesInstance(context);
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
//		view.setProgressBarVisibity(PROGRESS_BAR_INVISIBLE);
		mCursor = cursor;
		view.notifyViewPagerThatDataChanged();

		// Select the start ID
		if (mStartId > 0) {
			mCursor.moveToFirst();
			// TODO: optimize
			while (!mCursor.isAfterLast()) {
				if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
					final int position = mCursor.getPosition();
					view.setPagerPos(position);
					break;
				}
				mCursor.moveToNext();
			}
			mStartId = 0;
		}
	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {
		mCursor = null;
		view.notifyViewPagerThatDataChanged();
	}
}
