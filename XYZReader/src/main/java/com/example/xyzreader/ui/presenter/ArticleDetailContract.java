package com.example.xyzreader.ui.presenter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import com.example.xyzreader.data.loader.ArticleLoader;
import com.example.xyzreader.ui.view.BaseView;

public interface ArticleDetailContract {

	interface View extends BaseView, FragmentListener {

		void notifyViewPagerThatDataChanged();

		// TODO: 10/10/18 Verificar necessidade
		void updateUpBt(int position);

		void setPagerPos(int position);

		void startShareView(android.view.View view, Cursor cursor);

		void createPagerAdapter(Cursor cursor);

		void bindView(Cursor cursor);

		void swapCursor(Cursor cursor);
	}

	interface FragmentView {

		boolean isFragmentAdded();

		void bindView(Cursor mCursor);

		void setProgressBarVisibility(boolean visible);

		int getUpButtonFloor();

		void onUpButtonFloorChanged(long itemId);

		void updateStatusBar();
	}

	interface FragmentListener {
		void shareArticle(android.view.View v);

		void onUpBuutonFloorChanged(long itemId, int upButtonFloor);
	}

	interface PresenterFragment extends LoaderManager.LoaderCallbacks<Cursor>, ArticleLoader.LoaderListeners {

		void onScroolChanged(int mScrollY);

		long getArticleId();
	}

	interface Presenter extends LoaderManager.LoaderCallbacks<Cursor>, ArticleLoader.LoaderListeners {

		void restoreSavedState(Bundle savedInstanceState);

		void savePositionState(Bundle outState, int verticalScrollbarPosition);

		void setStartId(long mStartId);

		long getArticleIdByCursor();

		void shareArticle(android.view.View view);

		boolean onPageChange(int position);
	}
}
