package com.example.xyzreader.ui.presenter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import com.example.xyzreader.ui.view.BaseView;

public interface ArticleDetailContract {

	interface View extends BaseView, FragmentListener {

		void notifyViewPagerThatDataChanged();

		// TODO: 10/10/18 Verificar necessidade
		void updateUpBt(int position);

		void setPagerPos(int position);

		void startShareView(android.view.View view, Cursor cursor);
	}

	interface FragmentView {

		boolean isFragmentAdded();

		void bindView(Cursor mCursor);

		void setProgressBarVisibility(boolean visible);
	}

	interface FragmentListener {
		void shareArticle(android.view.View v);
	}

	interface PresenterFragment extends LoaderManager.LoaderCallbacks<Cursor> {

	}

	interface Presenter extends LoaderManager.LoaderCallbacks<Cursor> {

		void restoreSavedState(Bundle savedInstanceState);

		void savePositionState(Bundle outState, int verticalScrollbarPosition);

		void changeCursorPosition(int position);

		void setStartId(long mStartId);

		long getArticleIdByCursor();

		int getTotalInCursor();

		void shareArticle(android.view.View view);
	}
}
