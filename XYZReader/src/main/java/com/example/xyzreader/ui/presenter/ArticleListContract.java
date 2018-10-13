package com.example.xyzreader.ui.presenter;

import android.content.BroadcastReceiver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import com.example.xyzreader.data.model.Article;
import com.example.xyzreader.ui.view.BaseView;

public interface ArticleListContract {

	interface View extends BaseView {

		void showArticleDetailsView(Article article);

		void setArticleListPositionTo(int position);

		void createAdapter(Cursor cursor);

		void destroyList();
	}

	interface Presenter extends LoaderManager.LoaderCallbacks<Cursor> {

		void loadArticleList(BroadcastReceiver mRefreshingReceiver);

		void selectArticle(int position, Long id);

		void restoreSavedState(Bundle savedInstanceState);

		void savePositionState(Bundle outState, int verticalScrollbarPosition);
	}
}
